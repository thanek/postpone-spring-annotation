package net.schowek.xis.example.storage;

import net.schowek.xis.example.storage.InvocationDocument.Status;
import net.schowek.xis.spring.postpones.Invocation;

import static java.util.Arrays.stream;

public class InvocationDocumentMapper {
    static InvocationDocument toDocument(Invocation invocation, Status status) {
        InvocationDocument document = new InvocationDocument();
        document.setId(invocation.getId());
        document.setCreatedAt(invocation.getCreatedAt());
        document.setClazz(invocation.getClazz());
        document.setMethod(invocation.getMethod());
        document.setParameterTypes(
                stream(invocation.getParameterTypes()).map(Class::getCanonicalName).toArray(String[]::new));
        document.setArguments(invocation.getArguments());
        document.setStatus(status);
        return document;
    }

    static Invocation fromDocument(InvocationDocument document) {
        Class[] parameterTypes = stream(document.getParameterTypes()).map(InvocationDocumentMapper::getClass).toArray(Class[]::new);
        return new Invocation(document.getId(), document.getCreatedAt(), document.getClazz(),
                document.getMethod(), parameterTypes, document.getArguments());
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
