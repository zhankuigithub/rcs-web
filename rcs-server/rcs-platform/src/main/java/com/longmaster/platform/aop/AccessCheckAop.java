package com.longmaster.platform.aop;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.platform.cache.CacheHelper;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.entity.AccessCertificate;
import com.longmaster.platform.service.AccessCertificateService;
import com.longmaster.platform.service.RedisService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Aspect
//@Component
public class AccessCheckAop {

    @Resource
    private RedisService redisService;

    @Resource
    private AccessCertificateService accessCertificateService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final Long EXPIRE = 3600 * 2L;

    @Pointcut("@annotation(com.longmaster.platform.annotations.AccessCheck))")
    public void accessCheck() {
    }

    @Before("accessCheck()")
    public void before(JoinPoint joinPoint) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        String method = request.getMethod();
        String body = "";

        if (HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) {

            Map<String, Object> map = new HashMap<>();
            for (Object arg : joinPoint.getArgs()) {
                List<Field> list = Stream.of(arg.getClass().getDeclaredFields())
                        .collect(Collectors.toList());
                for (Field field : list) {
                    map.put(field.getName(), ReflectUtil.getFieldValue(arg, field.getName()));
                }
            }
            body = sortMapByKey(map);
        }

        if (HttpMethod.GET.name().equals(method) || HttpMethod.DELETE.name().equals(method)) {
            //body = request.getRequestURI();
            if (!StrUtil.isEmpty(request.getQueryString())) {
                body += "&" + request.getQueryString();
            }
        }

        // 构建加密体
        String sign = request.getHeader("sign");
        Assert.isFalse(StrUtil.isEmpty(sign), new ServerException("缺失参数sign！"));

        String appId = request.getHeader("appId");
        Assert.isFalse(StrUtil.isEmpty(appId), new ServerException("缺失参数appId！"));

        String ts = request.getHeader("ts");
        Assert.isFalse(StrUtil.isEmpty(ts), new ServerException("缺失参数ts！"));

        String key = CacheHelper.buildAccessCertificateCacheKey(GlobalCacheKeyDefine.ACCESS_CERTIFICATE, appId);

        AccessCertificate certificate = redisService.unblockingGet(key, EXPIRE,
                () -> accessCertificateService.getOne(new LambdaQueryWrapper<AccessCertificate>().eq(AccessCertificate::getAppId, appId)));

        Assert.notNull(certificate, new ServerException("找不到对应的凭证信息！"));

        String secret = certificate.getSecret();
        Assert.isFalse(StrUtil.isEmpty(secret), new ServerException("密钥信息错误！"));

        // 构建加密原文
        String format = "";
        if (HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) {
            format = String.format("appId=%s&secret=%s&ts=%s&body=%s", appId, secret, ts, body);
        }

        if (HttpMethod.GET.name().equals(method) || HttpMethod.DELETE.name().equals(method)) {
            format = String.format("appId=%s&secret=%s&ts=%s%s", appId, secret, ts, body);
        }

        String md5 = SecureUtil.md5(format);
        Assert.isTrue(md5.equals(sign), new ServerException("签名不合法！"));

    }

    private static <K, V> String sortMapByKey(Map<K, V> map) throws JsonProcessingException {
        return sObjectMapper.writeValueAsString(MapUtil.sort(map));
    }

}
