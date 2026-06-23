package com.cts.aspect;

import java.util.Arrays;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.cts.annotation.Audit;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.dto.AuditLogRequestDTO;
import com.cts.entity.User;
import com.cts.service.AuditLogService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final AuthenticatedUserProvider authUserProvider;

    // Runs when the method completes successfully → status = SUCCESS
    @AfterReturning(pointcut = "@annotation(auditAnnotation)", returning = "result")
    public void auditOnSuccess(JoinPoint joinPoint, Audit auditAnnotation, Object result) {
        try {
            User user         = authUserProvider.currentOrNull();
            String resourceId = resolveResourceId(joinPoint, result);

            AuditLogRequestDTO requestDTO = AuditLogRequestDTO.builder()
                    .userId(user != null ? user.getUserId() : null)
                    .action(auditAnnotation.action())
                    .resourceType(auditAnnotation.resourceType())
                    .resourceId(resourceId)
                    .status("SUCCESS")
                    .details(auditAnnotation.action() + " performed on "
                            + auditAnnotation.resourceType()
                            + (resourceId != null ? " [id=" + resourceId + "]" : ""))
                    .build();

            auditLogService.logAction(requestDTO);

            log.debug("Audit SUCCESS: action={}, resource={}, id={}",
                    auditAnnotation.action(), auditAnnotation.resourceType(), resourceId);

        } catch (Exception e) {
            log.error("Failed to record audit log: {}", e.getMessage());
        }
    }

    // Runs when the method throws an exception → status = FAILED
    @AfterThrowing(pointcut = "@annotation(auditAnnotation)", throwing = "ex")
    public void auditOnFailure(JoinPoint joinPoint, Audit auditAnnotation, Exception ex) {
        try {
            User user = authUserProvider.currentOrNull();

            AuditLogRequestDTO requestDTO = AuditLogRequestDTO.builder()
                    .userId(user != null ? user.getUserId() : null)
                    .action(auditAnnotation.action())
                    .resourceType(auditAnnotation.resourceType())
                    .resourceId(null)
                    .status("FAILED")
                    .details(auditAnnotation.action() + " FAILED on "
                            + auditAnnotation.resourceType()
                            + " | Reason: " + ex.getMessage())
                    .build();

            auditLogService.logAction(requestDTO);

            log.debug("Audit FAILED: action={}, resource={}, reason={}",
                    auditAnnotation.action(), auditAnnotation.resourceType(), ex.getMessage());

        } catch (Exception e) {
            log.error("Failed to record failure audit log: {}", e.getMessage());
        }
    }

    /**
     * Resolves the resource ID from:
     * 1. A Long / Integer argument on the method
     * 2. A getter ending in "Id" on the returned DTO (Integer or Long wrapper types)
     */
    private String resolveResourceId(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            Optional<Object> idFromArgs = Arrays.stream(args)
                    .filter(arg -> arg instanceof Long || arg instanceof Integer)
                    .findFirst();
            if (idFromArgs.isPresent()) return String.valueOf(idFromArgs.get());
        }

        if (result != null) {
            try {
                for (var method : result.getClass().getDeclaredMethods()) {
                    if (method.getName().startsWith("get") && method.getName().endsWith("Id")) {
                        Class<?> returnType = method.getReturnType();
                        if (returnType.equals(Integer.class) || returnType.equals(Long.class)) {
                            method.setAccessible(true);
                            Object id = method.invoke(result);
                            if (id != null) return String.valueOf(id);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}
