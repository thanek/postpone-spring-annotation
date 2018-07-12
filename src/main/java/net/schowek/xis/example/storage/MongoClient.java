package net.schowek.xis.example.storage;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoClient extends MongoRepository<InvocationDocument, String> {
}
