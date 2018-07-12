package net.schowek.xis.example.storage;

import net.schowek.xis.spring.postpones.Invocation;
import net.schowek.xis.spring.postpones.InvocationRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import static java.util.Arrays.stream;
import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class MongoInvocationRepository implements InvocationRepository {
    private static final Logger logger = getLogger(MongoInvocationRepository.class);

    private final MongoClient mongoClient;

    public MongoInvocationRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void save(Invocation invocation) {
        InvocationDocument doc = new InvocationDocument();
        doc.setId(invocation.getId());
        doc.setCreatedAt(invocation.getCreatedAt());
        doc.setClazz(invocation.getClazz());
        doc.setMethod(invocation.getMethod());
        doc.setParameterTypes(
                stream(invocation.getParameterTypes()).map(Class::getCanonicalName).toArray(String[]::new));
        doc.setArguments(invocation.getArguments());
        mongoClient.save(doc);
    }

    @Override
    public Invocation findFirst() {
        return mongoClient.findAll().stream().findAny()
                .map(d -> {
                    Class[] parameterTypes = stream(d.getParameterTypes()).map(className -> {
                        try {
                            return Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            logger.error("Could not find class", e);
                        }
                        return null;
                    }).toArray(Class[]::new);
                    return new Invocation(d.getId(), d.getCreatedAt(), d.getClazz(), d.getMethod(),
                            parameterTypes,
                            d.getArguments());
                }).orElse(null);
    }

    @Override
    public void markAsDone(Invocation invocation) {
        mongoClient.deleteById(invocation.getId());
    }
}
