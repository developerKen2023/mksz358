package com.ken.mksz358.feignApi.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class AuthAspect {

    public static final String ROLES = "roles";

    @Around("@annotation(com.ken.mksz358.feignApi.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint point) throws Throwable {
        // get header from RequestContextHolder
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        String roles = request.getHeader(ROLES);

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);

        if (!Objects.equals(roles, annotation.value()))
            throw new SecurityException("user has not privilege to access");
        return point.proceed();
    }
}
