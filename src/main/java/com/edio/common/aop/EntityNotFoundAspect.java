package com.edio.common.aop;

import com.edio.common.exception.base.ErrorMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@Order(1) // AOP 실행 우선순위 설정
@Slf4j
public class EntityNotFoundAspect {

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..)) && " +
            "execution(* *.findById*(..))")
    public Object handleFindById(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed(); // findById.. 실행

        if (result instanceof Optional<?> optional && optional.isEmpty()) { // 패턴 매칭 적용
            Object id = joinPoint.getArgs().length > 0 ? joinPoint.getArgs()[0] : "Unknown";
            String entityName = joinPoint.getSignature().getDeclaringType().getSimpleName();
            entityName = entityName.replace("Repository", "");

            log.warn("{} Not Found with ID: {}", entityName, id);
            throw new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(entityName, id));
        }
        return result;
    }
}
