package net.schowek.xis.spring.postpones;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

public class AnnotationsAwarePostponesInterceptor implements IntroductionInterceptor {
    private final Map<Object, Map<Method, MethodInterceptor>> delegates = new HashMap<>();
    private final ApplicationContext applicationContext;

    AnnotationsAwarePostponesInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInterceptor delegate = getDelegate(invocation.getThis(), invocation.getMethod());
        if (delegate != null) {
            return delegate.invoke(invocation);
        } else {
            return invocation.proceed();
        }
    }

    @Override
    public boolean implementsInterface(Class<?> intf) {
        return Postponable.class.isAssignableFrom(intf);
    }

    private MethodInterceptor getDelegate(Object target, Method method) {
        if (!this.delegates.containsKey(target) || !this.delegates.get(target).containsKey(method)) {
            synchronized (this.delegates) {
                if (!this.delegates.containsKey(target)) {
                    this.delegates.put(target, new HashMap<>());
                }
                Map<Method, MethodInterceptor> delegatesForTarget = this.delegates.get(target);
                if (!delegatesForTarget.containsKey(method)) {
                    Postponable postponable = AnnotationUtils.findAnnotation(method, Postponable.class);
                    if (postponable != null) {
                        InvocationRepository repository = applicationContext.getBean(postponable.repository());
                        MethodInterceptor delegate = new PostponesInterceptor(repository);
                        delegatesForTarget.put(method, delegate);
                    }
                }
            }
        }
        return this.delegates.get(target).get(method);
    }
}
