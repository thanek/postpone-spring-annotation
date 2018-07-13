package net.schowek.xis.example.storage;

import java.util.Optional;

import net.schowek.xis.spring.postpones.Invocation;
import net.schowek.xis.spring.postpones.InvocationRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static net.schowek.xis.example.storage.InvocationDocument.Status.RUNNING;
import static net.schowek.xis.example.storage.InvocationDocument.Status.WAITING;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.*;

@Repository
public class MongoInvocationRepository implements InvocationRepository {
    private final MongoTemplate mongoTemplate;

    public MongoInvocationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void add(Invocation invocation) {
        mongoTemplate.save(InvocationDocumentMapper.toDocument(invocation, WAITING));
    }

    @Override
    public Optional<Invocation> findFirst() {
        InvocationDocument document = mongoTemplate.findAndModify(
                query(where("status").is(WAITING)),
                update("status", RUNNING),
                InvocationDocument.class);

        return Optional.ofNullable(document).map(InvocationDocumentMapper::fromDocument);
    }

    @Override
    public void markAsDone(Invocation invocation) {
        mongoTemplate.remove(
                query(where("_id").is(invocation.getId())),
                InvocationDocument.class);
    }
}
