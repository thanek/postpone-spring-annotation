package net.schowek.xis.spring.postpones;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.core.annotation.AnnotationUtils;

public class AnnotationsAwarePostponesInterceptor implements IntroductionInterceptor {
    private final PostponedMethodsScanner postponedMethodsCache;

    AnnotationsAwarePostponesInterceptor(PostponedMethodsScanner postponedMethodsCache) {
        this.postponedMethodsCache = postponedMethodsCache;
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
        PostponesInterceptor postponesInterceptor = null;
        Postponable annotation = AnnotationUtils.findAnnotation(method, Postponable.class);
        if (annotation != null) {
            postponesInterceptor = postponedMethodsCache.getPostponesInterceptor(target, method);
        }
        return postponesInterceptor;
    }
}
