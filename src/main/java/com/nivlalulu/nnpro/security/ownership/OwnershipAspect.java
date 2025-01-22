package com.nivlalulu.nnpro.security.ownership;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.exceptions.UnauthorizedException;
import com.nivlalulu.nnpro.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OwnershipAspect {
    private final Map<Class<?>, IOwnershipChecker> checkers;

    @Pointcut("@annotation(isOwnedByUser)")
    public void isOwnedByUserAnnotationPointcut(IsOwnedByUser isOwnedByUser) {
        // pointcut
    }

    @Before("isOwnedByUserAnnotationPointcut(isOwnedByUser)")
    public void checkOwnership(JoinPoint jp, IsOwnedByUser isOwnedByUser) {
        var user = getUserFromSecurityContext();

        Class<?> entityClass = isOwnedByUser.entityClass();
        IOwnershipChecker checker = checkers.get(entityClass);
        if (checker == null) {
            log.error("No ownership checker found for entity class '{}'", entityClass);
            throw new IllegalArgumentException("No ownership checker found for entity class");
        }


        // 3)
        String paramName = isOwnedByUser.resourceIdParam();
        Object resourceId = getResourceId(jp, paramName);

        boolean exists = checker.resourceExists(resourceId);
        if (!exists) {
            throw new NotFoundException(entityClass.getSimpleName(), paramName, resourceId.toString());
        }

        boolean isOwned = checker.isOwnedBy(resourceId, user);
        if (!isOwned) {
            throw new UnauthorizedException("You do not own this " + entityClass.getSimpleName());
        }

        log.debug("User [{}] is verified as owner of {} with ID = {}.", user.getId(),
                entityClass.getSimpleName(), resourceId);
    }

    private static Object getResourceId(JoinPoint jp, String paramName) {
        Object[] args = jp.getArgs();
        MethodSignature sig = (MethodSignature) jp.getSignature();
        var params = sig.getMethod().getParameters();

        Object resourceId = null;
        for (int i = 0; i < params.length; i++) {
            if (params[i].getName().equals(paramName)) {
                resourceId = args[i];
                break;
            }
        }
        if (resourceId == null) {
            throw new IllegalArgumentException("Could not find param '" + paramName + "' in method arguments");
        }
        return resourceId;
    }

    private User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("User is not authenticated, are you using this annotation in a secured endpoint?");
            throw new UnauthorizedException("User is not authenticated");
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new UnauthorizedException("Unknown details.");
    }

}
