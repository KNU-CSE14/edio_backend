package com.edio.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.edio.*.controller..*(..)) || execution(* com.edio.*.*.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info(
                "API Call - Method: {}, Duration: {} ms, Start Time: {}, End Time: {}, Arguments: {}",
                joinPoint.getSignature(),
                duration,
                startTime,
                endTime,
                Arrays.toString(joinPoint.getArgs())
        );

        return proceed;
    }

}
