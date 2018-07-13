package net.schowek.xis.spring.postpones;

import java.time.Instant;
import java.util.Arrays;

public class Invocation {
    private final String id;
    private final Instant createdAt;
    private final String methodQualifier;
    private final Object[] arguments;

    public Invocation(String id, Instant createdAt, String methodQualifier, Object[] arguments) {
        this.id = id;
        this.createdAt = createdAt;
        this.methodQualifier = methodQualifier;
        this.arguments = arguments;
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getMethodQualifier() {
        return methodQualifier;
    }

    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "Invocation{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", methodQualifier='" + methodQualifier + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
