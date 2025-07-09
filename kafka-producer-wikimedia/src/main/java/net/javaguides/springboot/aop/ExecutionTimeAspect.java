package net.javaguides.springboot.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("@annotation(net.javaguides.springboot.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();  // 원래 메서드 실행

        long duration = System.currentTimeMillis() - start;
        long threadId = Thread.currentThread().getId();
        String threadName = Thread.currentThread().getName();

        LOGGER.info("🧵 [Thread ID: {}, Name: {}] {} 수행 시간: {}ms",
                threadId,
                threadName,
                joinPoint.getSignature(),
                duration
        );
        return proceed;
    }
}