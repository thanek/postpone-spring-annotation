package net.schowek.xis.example.storage;

import net.schowek.xis.spring.postpones.Invocation;

import static java.util.Arrays.stream;

public class InvocationDocumentMapper {
    public static InvocationDocument toDocument(Invocation invocation) {
        InvocationDocument document = new InvocationDocument();
        document.setId(invocation.getId());
        document.setCreatedAt(invocation.getCreatedAt());
        document.setClazz(invocation.getClazz());
        document.setMethod(invocation.getMethod());
        document.setParameterTypes(
                stream(invocation.getParameterTypes()).map(Class::getCanonicalName).toArray(String[]::new));
        document.setArguments(invocation.getArguments());
        return document;
    }

    public static Invocation fromDocument(InvocationDocument document) {
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
