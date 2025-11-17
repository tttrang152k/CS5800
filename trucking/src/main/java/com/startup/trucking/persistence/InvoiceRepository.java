package com.startup.trucking.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    Optional<Invoice> findByLoadId(String loadId);
    List<Invoice> findByStatus(String status);
}
