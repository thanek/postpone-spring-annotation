package net.schowek.xis.spring.postpones;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.UUID;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class PostponesInterceptor implements MethodInterceptor {
    private static final Logger logger = getLogger(PostponesInterceptor.class);
    private final InvocationRepository repository;

    PostponesInterceptor(InvocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Class clazz = method.getDeclaringClass();
        String name = method.getName();

        Invocation invocation = new Invocation(UUID.randomUUID().toString(),
                Instant.now(),
                clazz.getCanonicalName(), name, method.getParameterTypes(), methodInvocation.getArguments());

        logger.info("Postponing invocation {}", invocation);
        repository.save(invocation);

        return null;
    }
}
