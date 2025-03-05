package com.edio.common.aop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SoftDeleteFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    // Repository 패키지 경로에 맞게 포인트컷 지정
    @Around("execution(* com.edio.studywithcard..repository.*.*(..))")
    public Object applySoftDeleteFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("softDeleteFilter");
        filter.setParameter("isDeleted", false);
        try {
            return joinPoint.proceed();
        } finally {
            // 필요 시, 필터를 비활성화할 수 있습니다.
            // session.disableFilter("softDeleteFilter");
        }
    }
}