package net.schowek.xis.spring.postpones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import static org.slf4j.LoggerFactory.getLogger;

public class PostponedOperationsInvoker {
    private static final Logger logger = getLogger(PostponedOperationsInvoker.class);
    private final InvocationRepository repository;
    private final PostponedMethodsScanner postponedMethods;

    public PostponedOperationsInvoker(InvocationRepository repository, PostponedMethodsScanner postponedMethods) {
        this.repository = repository;
        this.postponedMethods = postponedMethods;
    }

    public void invokeQueued() {
        repository.findFirst().ifPresent(invocation -> {
            try {
                invokeMethod(invocation);
                repository.markAsDone(invocation);
            } catch (InvocationTargetException e) {
                ReflectionUtils.rethrowRuntimeException(e.getTargetException());
            } catch (IllegalAccessException e) {
                logger.error("Could not invoke {}:", invocation, e);
            }
        });
    }

    private void invokeMethod(Invocation invocation) throws IllegalAccessException, InvocationTargetException {
        PostponedMethod postponedMethod = postponedMethods.get(invocation.getMethodQualifier());
        Object bean = postponedMethod.getTargetBean();
        Advisor postponeAdvisor = null;
        try {
            if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
                postponeAdvisor = temporaryDisablePostponeAdvisor((Advised) bean);
            }

            logger.debug("Invoking {} on target {}", invocation, bean.getClass());
            Method method = postponedMethod.getMethod();
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean, invocation.getArguments());
        } finally {
            if (postponeAdvisor != null) {
                ((Advised) bean).addAdvisor(postponeAdvisor);
            }
        }
    }

    private Advisor temporaryDisablePostponeAdvisor(Advised bean) {
        for (Advisor advisor : bean.getAdvisors()) {
            if (advisor instanceof PostponesConfiguration) {
                bean.removeAdvisor(advisor);
                return advisor;
            }
        }
        return null;
    }
}
