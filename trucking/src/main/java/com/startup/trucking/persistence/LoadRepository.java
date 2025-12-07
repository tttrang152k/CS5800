package com.startup.trucking.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoadRepository extends JpaRepository<LoadEntity, String> {
    List<LoadEntity> findByReferenceNoContainingIgnoreCase(String referenceNo);
    List<LoadEntity> findByStatusIgnoreCase(String status);
}
