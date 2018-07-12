package net.schowek.xis.spring.postpones;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class MethodInvocator {
    private final ApplicationContext applicationContext;
    private final InvocationRepository repository;
    private static final Logger logger = getLogger(MethodInvocator.class);


    public MethodInvocator(ApplicationContext applicationContext, InvocationRepository repository) {
        this.applicationContext = applicationContext;
        this.repository = repository;
    }

    public void invokeQueuedMethods() throws Exception {
        Invocation invocation = repository.findFirst();
        if (invocation == null) {
            return;
        }

        Object bean = applicationContext.getBean(Class.forName(invocation.getClazz()));
        Object target;
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            target = ((Advised) bean).getTargetSource().getTarget();
        } else {
            target = bean;
        }
        Method method = target.getClass().getDeclaredMethod(invocation.getMethod(), invocation.getParameterTypes());
        logger.info("Invoking {} on target {}", invocation, target.getClass());
        method.invoke(target, invocation.getArguments());
        repository.markAsDone(invocation);
    }
}
