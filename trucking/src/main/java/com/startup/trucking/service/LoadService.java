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

    private static final String FIELD_ID = "id";
    private static final String FIELD_CUSTOMER = "customer";
    private static final String FIELD_STATUS = "status";

    private final LoadRepository repository;
    private final LoadSortStrategyResolver sortResolver;

    public LoadService(LoadRepository repository, LoadSortStrategyResolver sortResolver) {
        this.repository = repository;
        this.sortResolver = sortResolver;
    }

    public Load getLoad(String loadId) {
        return repository.findById(loadId)
                .map(LoadMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));
    }

    @Transactional
    public void putLoad(Load load) {
        if (load == null || isBlank(load.getId())) {
            throw new IllegalArgumentException("load.id must not be blank");
        }
        repository.save(LoadMapper.toEntity(load));
    }

    public Collection<Load> listLoads() {
        return repository.findAll().stream()
                .map(LoadMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Load> listLoadsSorted(String sortKey) {
        List<Load> loads = repository.findAll().stream()
                .map(LoadMapper::toDomain)
                .collect(Collectors.toList());
        return sortResolver.sort(loads, sortKey);
    }

    @Transactional
    public void updateStatus(String loadId, String status) {
        LoadEntity entity = repository.findById(loadId)
                .orElseThrow(() -> new IllegalArgumentException("Load not found: " + loadId));
        entity.setStatus(status);
        repository.save(entity);
    }

    public List<Load> searchLoads(String query, String field) {
        if (isBlank(query)) {
            return List.of();
        }

        String trimmedQuery = query.trim();
        String normalizedField = normalizeField(field);

        return switch (normalizedField) {
            case FIELD_ID -> searchById(trimmedQuery);
            case FIELD_CUSTOMER -> searchByCustomer(trimmedQuery);
            case FIELD_STATUS -> searchByStatus(trimmedQuery);
            default -> searchAcrossAllFields(trimmedQuery);
        };
    }

    // ---------------------------------------------------------------------
    // Search helpers
    // ---------------------------------------------------------------------

    private List<Load> searchById(String id) {
        return repository.findById(id)
                .map(List::of)
                .map(this::mapEntitiesToDomain)
                .orElseGet(List::of);
    }

    private List<Load> searchByCustomer(String customerQuery) {
        List<LoadEntity> entities =
                repository.findByReferenceNoContainingIgnoreCase(customerQuery);
        return mapEntitiesToDomain(entities);
    }

    private List<Load> searchByStatus(String status) {
        List<LoadEntity> entities = repository.findByStatusIgnoreCase(status);
        return mapEntitiesToDomain(entities);
    }

    private List<Load> searchAcrossAllFields(String query) {
        List<LoadEntity> allMatches = new ArrayList<>();

        repository.findById(query).ifPresent(allMatches::add);
        allMatches.addAll(repository.findByReferenceNoContainingIgnoreCase(query));
        allMatches.addAll(repository.findByStatusIgnoreCase(query));

        List<LoadEntity> deduped = deduplicateById(allMatches);
        return mapEntitiesToDomain(deduped);
    }

    // ---------------------------------------------------------------------
    // Utility helpers
    // ---------------------------------------------------------------------

    private String normalizeField(String field) {
        if (isBlank(field)) {
            return FIELD_ID;
        }
        return field.trim().toLowerCase(Locale.ROOT);
    }

    private List<Load> mapEntitiesToDomain(List<LoadEntity> entities) {
        return entities.stream()
                .map(LoadMapper::toDomain)
                .collect(Collectors.toList());
    }

    private List<LoadEntity> deduplicateById(List<LoadEntity> entities) {
        Map<String, LoadEntity> byId = new LinkedHashMap<>();
        for (LoadEntity entity : entities) {
            if (entity.getId() == null) {
                continue;
            }
            byId.putIfAbsent(entity.getId(), entity);
        }
        return new ArrayList<>(byId.values());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
