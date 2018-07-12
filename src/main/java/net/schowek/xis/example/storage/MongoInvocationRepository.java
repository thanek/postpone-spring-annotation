package net.schowek.xis.example.storage;

import java.util.Optional;
import net.schowek.xis.spring.postpones.Invocation;
import net.schowek.xis.spring.postpones.InvocationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MongoInvocationRepository implements InvocationRepository {
    private final MongoClient mongoClient;

    public MongoInvocationRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void save(Invocation invocation) {
        mongoClient.save(InvocationDocumentMapper.toDocument(invocation));
    }

    @Override
    public Optional<Invocation> findFirst() {
        return mongoClient.findAll().stream().findAny()
                .map(InvocationDocumentMapper::fromDocument);
    }

    @Override
    public void markAsDone(Invocation invocation) {
        mongoClient.deleteById(invocation.getId());
    }
}
