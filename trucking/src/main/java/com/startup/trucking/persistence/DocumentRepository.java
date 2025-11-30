package com.startup.trucking.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, String> {
    List<Document> findByLoadIdOrderByUploadedAtDesc(String loadId);
}
