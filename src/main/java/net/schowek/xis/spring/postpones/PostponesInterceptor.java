package net.schowek.xis.spring.postpones;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

public class PostponesInterceptor implements MethodInterceptor {
    private static final Logger logger = getLogger(PostponesInterceptor.class);
    private final InvocationRepository repository;
    private final String methodQualifier;

    PostponesInterceptor(InvocationRepository repository, String methodQualifier) {
        this.repository = repository;
        this.methodQualifier = methodQualifier;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Invocation invocation =
                new Invocation(randomUUID().toString(), now(), methodQualifier, methodInvocation.getArguments());

        logger.debug("Postponing invocation {}", invocation);
        repository.add(invocation);

        return null;
    }
}
