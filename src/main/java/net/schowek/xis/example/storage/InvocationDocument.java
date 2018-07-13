package net.schowek.xis.example.storage;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invocations")
public class InvocationDocument {
    @Id
    private String id;
    private Instant createdAt;
    private String methodQualifier;
    private Object[] arguments;
    private Status status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getMethodQualifier() {
        return methodQualifier;
    }

    public void setMethodQualifier(String methodQualifier) {
        this.methodQualifier = methodQualifier;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        WAITING, RUNNING
    }
}
