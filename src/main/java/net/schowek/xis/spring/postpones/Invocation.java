package net.schowek.xis.spring.postpones;

import java.time.Instant;
import java.util.Arrays;

public class Invocation {
    private final String id;
    private final Instant createdAt;
    private final String clazz;
    private final String method;
    private final Class[] parameterTypes;
    private final Object[] arguments;

    public Invocation(String id, Instant createdAt, String clazz, String method, Class[] parameterTypes, Object[] arguments) {
        this.id = id;
        this.createdAt = createdAt;
        this.clazz = clazz;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public String getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "Invocation{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", clazz='" + clazz + '\'' +
                ", method='" + method + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
