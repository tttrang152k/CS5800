package com.startup.trucking.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByCustomerRefOrderByCreatedAtDesc(String customerRef);
}