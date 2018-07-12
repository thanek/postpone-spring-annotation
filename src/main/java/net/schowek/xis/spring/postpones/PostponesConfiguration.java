package net.schowek.xis.spring.postpones;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

@Configuration
public class PostponesConfiguration extends AbstractPointcutAdvisor implements IntroductionAdvisor, BeanFactoryAware {
    private Pointcut pointcut;
    private Advice advice;
    private BeanFactory beanFactory;
    private final ApplicationContext applicationContext;

    public PostponesConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        Set<Class<? extends Annotation>> repeatableAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(1);
        repeatableAnnotationTypes.add(Postponable.class);
        this.pointcut = buildPointcut(repeatableAnnotationTypes);
        this.advice = buildAdvice();
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    @Override
    public ClassFilter getClassFilter() {
        return pointcut.getClassFilter();
    }

    @Override
    public void validateInterfaces() throws IllegalArgumentException {
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public Class<?>[] getInterfaces() {
        return new Class[]{Postponable.class};
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private Advice buildAdvice() {
        return new AnnotationsAwarePostponesInterceptor(applicationContext);
    }

    /**
     * Calculate a pointcut for the given retry postpones types, if any.
     *
     * @param repeatAnnotationTypes the retry postpones types to introspect
     * @return the applicable Pointcut object, or {@code null} if none
     */
    private Pointcut buildPointcut(Set<Class<? extends Annotation>> repeatAnnotationTypes) {
        ComposablePointcut result = null;
        for (Class<? extends Annotation> repeatAnnotationType : repeatAnnotationTypes) {
            Pointcut filter = new AnnotationClassOrMethodPointcut(repeatAnnotationType);
            if (result == null) {
                result = new ComposablePointcut(filter);
            } else {
                result.union(filter);
            }
        }
        return result;
    }

    private final class AnnotationClassOrMethodPointcut extends StaticMethodMatcherPointcut {

        private final MethodMatcher methodResolver;

        AnnotationClassOrMethodPointcut(Class<? extends Annotation> annotationType) {
            this.methodResolver = new AnnotationMethodMatcher(annotationType);
            setClassFilter(new AnnotationClassOrMethodFilter(annotationType));
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return getClassFilter().matches(targetClass) || this.methodResolver.matches(method, targetClass);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationClassOrMethodPointcut)) {
                return false;
            }
            AnnotationClassOrMethodPointcut otherAdvisor = (AnnotationClassOrMethodPointcut) other;
            return ObjectUtils.nullSafeEquals(this.methodResolver, otherAdvisor.methodResolver);
        }

    }

    private final class AnnotationClassOrMethodFilter extends AnnotationClassFilter {

        private final AnnotationMethodsResolver methodResolver;

        AnnotationClassOrMethodFilter(Class<? extends Annotation> annotationType) {
            super(annotationType, true);
            this.methodResolver = new AnnotationMethodsResolver(annotationType);
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return super.matches(clazz) || this.methodResolver.hasAnnotatedMethods(clazz);
        }

    }

    private static class AnnotationMethodsResolver {

        private Class<? extends Annotation> annotationType;

        public AnnotationMethodsResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean hasAnnotatedMethods(Class<?> clazz) {
            final AtomicBoolean found = new AtomicBoolean(false);
            ReflectionUtils.doWithMethods(clazz, method -> {
                if (found.get()) {
                    return;
                }
                Annotation annotation = AnnotationUtils.findAnnotation(method, annotationType);
                if (annotation != null) {
                    found.set(true);
                }
            });
            return found.get();
        }

    }
}
