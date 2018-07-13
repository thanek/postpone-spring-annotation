package net.schowek.xis.example.storage;

import net.schowek.xis.example.storage.InvocationDocument.Status;
import net.schowek.xis.spring.postpones.Invocation;

public class InvocationDocumentMapper {
    static InvocationDocument toDocument(Invocation invocation, Status status) {
        InvocationDocument document = new InvocationDocument();
        document.setId(invocation.getId());
        document.setCreatedAt(invocation.getCreatedAt());
        document.setMethodQualifier(invocation.getMethodQualifier());
        document.setArguments(invocation.getArguments());
        document.setStatus(status);
        return document;
    }

    static Invocation fromDocument(InvocationDocument document) {
        return new Invocation(document.getId(), document.getCreatedAt(), document.getMethodQualifier(), document.getArguments());
    }
}
