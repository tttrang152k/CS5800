package com.startup.trucking.service;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.LoadEntity;
import com.startup.trucking.persistence.LoadMapper;
import com.startup.trucking.persistence.LoadRepository;
import com.startup.trucking.service.sort.LoadSortStrategyResolver;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoadService {
    private final LoadRepository repo;
    private final LoadSortStrategyResolver sortResolver;

    public LoadService(LoadRepository repo, LoadSortStrategyResolver sortResolver) {
        this.repo = repo;
        this.sortResolver = sortResolver;
    }

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

    // New feature: Load Sorting
    public List<Load> listLoadsSorted(String sortKey) {
        List<Load> loads = repo.findAll().stream()
                .map(LoadMapper::toDomain)
                .collect(Collectors.toList());
        return sortResolver.sort(loads, sortKey);
    }

    @Transactional
    public void updateStatus(String loadId, String status) {
        LoadEntity e = repo.findById(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));
        e.setStatus(status);
        repo.save(e);
    }

    // New Feature: Load Searching (by ID, customer name, or status)
    public List<Load> searchLoads(String query, String field) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.trim();

        List<LoadEntity> entities = new ArrayList<>();

        switch (field) {
            case "id" -> {
                repo.findById(q).ifPresent(entities::add);
            }
            case "customer" -> {
                entities.addAll(repo.findByReferenceNoContainingIgnoreCase(q));
            }
            case "status" -> {
                entities.addAll(repo.findByStatusIgnoreCase(q));
            }
            default -> {
                // fallback: search all three
                repo.findById(q).ifPresent(entities::add);
                entities.addAll(repo.findByReferenceNoContainingIgnoreCase(q));
                entities.addAll(repo.findByStatusIgnoreCase(q));

                // deduplicate by id
                Map<String, LoadEntity> byId = new LinkedHashMap<>();
                for (LoadEntity e : entities) {
                    byId.putIfAbsent(e.getId(), e);
                }
                entities = new ArrayList<>(byId.values());
            }
        }

        return entities.stream()
                .map(LoadMapper::toDomain)
                .collect(Collectors.toList());
    }
}

