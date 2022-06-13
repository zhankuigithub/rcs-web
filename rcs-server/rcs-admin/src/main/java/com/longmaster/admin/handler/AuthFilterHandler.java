package com.longmaster.admin.handler;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.admin.annotations.Auth;
import com.longmaster.admin.cache.Session;
import com.longmaster.admin.dto.auth.AuthAdminDto;
import com.longmaster.admin.entity.AuthRoles;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.AuthException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Order(0)
@Aspect
@Component
public class AuthFilterHandler {

    @Resource
    private RedisTemplate redisTemplate;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Pointcut("@annotation(com.longmaster.admin.annotations.Auth)")
    public void point() {
    }

    @Around("point()&&@annotation(auth)")
    public Object around(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
        if (auth.session()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String accessToken = request.getHeader(AuthConstant.TOKEN_KEY);
            Assert.isTrue(StrUtil.isNotBlank(accessToken), new AuthException("请提供有效凭证访问服务！(NO TOKEN)"));
            Assert.isTrue(redisTemplate.hasKey(accessToken), new AuthException("登录过期，请重新登录！", Result.TOKEN_INVALID(null)));
            AuthAdminDto admin = sObjectMapper.readValue((String) redisTemplate.opsForValue().get(accessToken), AuthAdminDto.class);
            Session.cache(admin);
        }
        if (auth.roles().length > 0) {
            List<AuthRoles> roles = Session.get().getRoles();
            if (roles != null) {
                for (String role : auth.roles()) {
                    if (roles.contains(role)) {
                        return joinPoint.proceed();
                    }
                }
                throw new AuthException("您的权限不够，无法访问~");
            }
        }
        return joinPoint.proceed();
    }
}
