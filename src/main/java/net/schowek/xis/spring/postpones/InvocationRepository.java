package net.schowek.xis.spring.postpones;

public interface InvocationRepository {
    void save(Invocation invocation);

    Invocation findFirst();

    void markAsDone(Invocation invocation);
}
