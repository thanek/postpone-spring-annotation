package net.schowek.xis.spring.postpones;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static java.util.Arrays.stream;
import static net.schowek.xis.spring.postpones.PostponedMethod.defaultQualifier;
import static org.springframework.aop.support.AopUtils.isAopProxy;
import static org.springframework.aop.support.AopUtils.isCglibProxy;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class PostponedMethodsScanner {
    private static final Logger logger = LoggerFactory.getLogger(PostponedMethodsScanner.class);
    private final ApplicationContext applicationContext;
    private Map<String, PostponedMethod> qualifiers = new HashMap<>();
    private Map<String, PostponesInterceptor> postponesInterceptors = new HashMap<>();

    @Autowired
    public PostponedMethodsScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws BeansException {
        InvocationRepository repository =
                stream(applicationContext.getBeanNamesForAnnotation(EnablePostpones.class))
                        .findAny().map(beanName -> {
                    EnablePostpones annotation = applicationContext.findAnnotationOnBean(beanName, EnablePostpones.class);
                    return applicationContext.getBean(annotation.repository());
                }).orElse(null);

        for (Object bean : applicationContext.getBeansOfType(Object.class).values()) {
            Class<?> beanClass = bean.getClass();
            if (isAopProxy(bean) || isCglibProxy(bean)) {
                beanClass = AopUtils.getTargetClass(bean);
            }

            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Postponable.class)) {
                    Postponable annotation = method.getAnnotation(Postponable.class);

                    PostponedMethod postponedMethod = new PostponedMethod(bean, method);
                    String qualifier = getQualifier(annotation.methodQualifier(), postponedMethod);
                    qualifiers.put(qualifier, postponedMethod);

                    postponesInterceptors.put(defaultQualifier(postponedMethod),
                            new PostponesInterceptor(repository, qualifier));
                }
            }
        }
        logger.debug("Found {} Postponable annotated methods", qualifiers.size());
    }

    PostponedMethod get(String qualifier) {
        return qualifiers.get(qualifier);
    }

    PostponesInterceptor getPostponesInterceptor(Object targetBean, Method method) {
        return postponesInterceptors.get(defaultQualifier(new PostponedMethod(targetBean, method)));
    }

    private String getQualifier(String qualifier, PostponedMethod postponedMethod) {
        return isEmpty(qualifier) ? defaultQualifier(postponedMethod) : qualifier;
    }
}
