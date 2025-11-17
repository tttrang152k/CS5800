package com.startup.trucking.service;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.LoadEntity;
import com.startup.trucking.persistence.LoadMapper;
import com.startup.trucking.persistence.LoadRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LoadService {
    private final LoadRepository repo;

    public LoadService(LoadRepository repo) { this.repo = repo; }

    public Load getLoad(String loadId) {
        return repo.findById(loadId)
                .map(LoadMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));
    }

    @Transactional
    public void putLoad(Load load) {
        if (load == null || load.getId() == null || load.getId().isBlank()) {
            throw new IllegalArgumentException("load.id must not be blank");
        }
        repo.save(LoadMapper.toEntity(load));
    }

    public Collection<Load> listLoads() {
        return repo.findAll().stream().map(LoadMapper::toDomain).collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(String loadId, String status) {
        LoadEntity e = repo.findById(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));
        e.setStatus(status);
        repo.save(e);
    }
}
