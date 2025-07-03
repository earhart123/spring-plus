package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.annotation.ManagerSaveFailureLogging;
import org.example.expert.domain.manager.service.LogService;
import org.springframework.stereotype.Component;

/**
 * 담당자 등록 시스템 로깅 AOP
 */

@Aspect
@Component
@RequiredArgsConstructor
public class ManagerSaveLoggingAspect {

    private final LogService logService;

    @Around("@annotation(logging)")
    public Object logOnException(ProceedingJoinPoint joinPoint, ManagerSaveFailureLogging logging) throws Throwable {

        try {
            Object result = joinPoint.proceed();

            // 담당자 등록 성공
            logService.saveLog("성공", "정상 등록 완료");
            return result;
        } catch (Exception e) {
            // 예외 발생으로 담당자 등록 실패
            logService.saveLog("실패", "사유: " + e.getMessage());
            throw e;
        }
    }
}
