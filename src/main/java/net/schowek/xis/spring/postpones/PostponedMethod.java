package net.schowek.xis.spring.postpones;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.aop.support.AopUtils.isAopProxy;

public class PostponedMethod {
    private final Object targetBean;
    private final Method method;

    public PostponedMethod(Object targetBean, Method method) {
        this.targetBean = targetBean;
        this.method = method;
    }

    public Object getTargetBean() {
        return targetBean;
    }

    public Method getMethod() {
        return method;
    }

    public static String defaultQualifier(PostponedMethod postponedMethod) {
        Object bean = postponedMethod.targetBean;
        Class<?> beanClass = bean.getClass();
        if (isAopProxy(bean)) {
            beanClass = getTargetClass(bean);
        }
        return beanClass.getCanonicalName() + "::" + postponedMethod.method.getName() +
                "(" + asList(postponedMethod.method.getParameterTypes()) + ")";
    }
}
