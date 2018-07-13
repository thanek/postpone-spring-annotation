package net.schowek.xis.spring.postpones;

import java.util.Optional;

public interface InvocationRepository {
    void add(Invocation invocation);

    Optional<Invocation> findFirst();

    void markAsDone(Invocation invocation);
}
