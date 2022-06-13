package com.longmaster.platform.aop;

import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.platform.service.RedisService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@Aspect
//@Component
public class AccessTokenAop {

    @Resource
    private RedisService redisService;

    @Pointcut("execution(* com.longmaster.platform.controller..*.*(..))")
    public void accessToken() {
    }

    @Pointcut("execution(public * com.longmaster.platform.controller.api.AccessCertificateController.*(..))")
    public void exclude1() {
    }

    @Pointcut("execution(public * com.longmaster.platform.controller.manage.ContactInformationController.*(..))")
    public void exclude2() {
    }

    @Pointcut("execution(public * com.longmaster.platform.controller.manage.NewsInfoController.*(..))")
    public void exclude3() {
    }


    @Before("accessToken() && !exclude1() && !exclude2() && !exclude3()")
    public void before(JoinPoint joinPoint) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        String authorization = request.getHeader("authorization");
        Assert.notNull(authorization, new ServerException("缺失参数authorization"));

        String appId = request.getHeader("appId");
        Assert.notNull(appId, new ServerException("缺失参数appId"));

        boolean exist = redisService.isExist(authorization);

        Assert.isTrue(exist, new ServerException("不合法的 accessToken"));

        String cache = redisService.get(authorization);
        Assert.isTrue(cache.equals(appId), new ServerException("不合法的 accessToken"));
    }
}
