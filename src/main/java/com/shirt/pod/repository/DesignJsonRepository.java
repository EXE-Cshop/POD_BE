package com.shirt.pod.repository;

import com.shirt.pod.model.document.DesignJsonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignJsonRepository extends MongoRepository<DesignJsonDocument, String> {
}
