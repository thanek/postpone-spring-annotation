package net.schowek.xis.spring.postpones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PostponedMethodInvoker {
    private static final Logger logger = getLogger(PostponedMethodInvoker.class);
    private final ApplicationContext applicationContext;
    private final InvocationRepository repository;
    private final Map<Class, Object> beansCache = new HashMap<>();

    @Autowired
    public PostponedMethodInvoker(ApplicationContext applicationContext, InvocationRepository repository) {
        this.applicationContext = applicationContext;
        this.repository = repository;
    }

    public void invokeQueued() {
        repository.findFirst().ifPresent(invocation -> {
            try {
                invokeMethod(invocation);
                repository.markAsDone(invocation);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                logger.error("Could not invoke {}:", invocation, e);
            }
        });
    }

    private void invokeMethod(Invocation invocation) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object bean = getTargetBean(invocation);
        Advisor postponeAdvisor = null;
        try {
            if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
                postponeAdvisor = temporaryDisablePostponeAdvisor((Advised) bean);
            }

            logger.info("Invoking {} on target {}", invocation, bean.getClass());
            Method method = getDeclaredMethod(invocation, bean);
            method.invoke(bean, invocation.getArguments());
        } finally {
            if (postponeAdvisor != null) {
                ((Advised) bean).addAdvisor(postponeAdvisor);
            }
        }
    }

    private Method getDeclaredMethod(Invocation invocation, Object bean) throws NoSuchMethodException {
        return bean.getClass().getDeclaredMethod(invocation.getMethod(), invocation.getParameterTypes());
    }

    private Object getTargetBean(Invocation invocation) throws ClassNotFoundException {
        Class<?> beanClass = Class.forName(invocation.getClazz());
        if (!beansCache.containsKey(beanClass)) {
            beansCache.put(beanClass, applicationContext.getBean(beanClass));
        }

        return beansCache.get(beanClass);
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
