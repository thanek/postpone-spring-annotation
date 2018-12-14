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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ObjectUtils;

@Configuration
public class PostponesConfiguration extends AbstractPointcutAdvisor implements ImportAware {
    private Pointcut pointcut;
    private Advice advice;
    private final ApplicationContext applicationContext;
    @Nullable
    private AnnotationAttributes enablePostpones;

    public PostponesConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enablePostpones = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnablePostpones.class.getName(), false));

        if (this.enablePostpones == null) {
            throw new IllegalArgumentException(
                    "@EnablePostpones is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public PostponedMethodsScanner postponedMethodsScanner() {
        return new PostponedMethodsScanner(applicationContext);
    }

    @Bean
    public PostponedOperationsInvoker postponedOperationsInvoker() {
        return new PostponedOperationsInvoker(applicationContext.getBean(InvocationRepository.class), postponedMethodsScanner());
    }

    @Bean
    public TaskExecutor postponedOperationsThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("postponed-operations-invoker");
        executor.initialize();
        return executor;
    }

    @Bean
    public AutoPostponedOperationsInvoker autoPostponedOperationsInvoker() {
        boolean autoInvoke = this.enablePostpones.getBoolean("autoInvoke");
        long sleepTime = this.enablePostpones.getNumber("sleepTime").longValue();
        return new AutoPostponedOperationsInvoker(postponedOperationsInvoker(),
                postponedOperationsThreadPoolTaskExecutor(), autoInvoke, sleepTime);
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
        return new AnnotationsAwarePostponesInterceptor(postponedMethodsScanner());
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
