package com.shirt.pod.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * MongoDB document storing full design JSON (Fabric.js serialized).
 * DesignProduct in PostgreSQL holds metadata + design_json_ref pointing here.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "design_jsons")
public class DesignJsonDocument {

    @Id
    private String id;

    private Map<String, Object> data;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
