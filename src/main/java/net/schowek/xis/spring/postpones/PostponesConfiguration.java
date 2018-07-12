package net.schowek.xis.spring.postpones;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import org.aopalliance.aop.Advice;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
public class PostponesConfiguration extends AbstractPointcutAdvisor {
    private Pointcut pointcut;
    private Advice advice;
    private final ApplicationContext applicationContext;

    public PostponesConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        this.advice = buildAdvice();
        this.pointcut = buildPointcut();
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    private Advice buildAdvice() {
        return new AnnotationsAwarePostponesInterceptor(applicationContext);
    }

    private Pointcut buildPointcut() {
        Pointcut pointcut = new AnnotationMethodPointcut(Postponable.class);
        return new ComposablePointcut(pointcut);
    }

    private final class AnnotationMethodPointcut extends StaticMethodMatcherPointcut {

        private final MethodMatcher methodResolver;

        AnnotationMethodPointcut(Class<? extends Annotation> annotationType) {
            this.methodResolver = new AnnotationMethodMatcher(annotationType);
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return this.methodResolver.matches(method, targetClass);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationMethodPointcut)) {
                return false;
            }
            AnnotationMethodPointcut otherAdvisor = (AnnotationMethodPointcut) other;
            return ObjectUtils.nullSafeEquals(this.methodResolver, otherAdvisor.methodResolver);
        }
    }
}
