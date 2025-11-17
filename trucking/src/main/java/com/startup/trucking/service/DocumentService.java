package com.startup.trucking.service;

import com.startup.trucking.domain.Document;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentService {
    private final Map<String, Document> store = new ConcurrentHashMap<>();
    private static final Set<String> ALLOWED = Set.of("BOL", "POD");

    public String upload(String loadId, String type, URI fileRef) {
        if (fileRef == null) throw new IllegalArgumentException("fileRef required");
        if (!ALLOWED.contains(type)) {
            throw new IllegalArgumentException("Only BOL/POD allowed");
        }
        String id = "DOC-" + UUID.randomUUID();
        store.put(id, new Document(id, loadId, type, "Uploaded", fileRef));
        return id;
    }

    public List<Document> list(String loadId) {
        List<Document> out = new ArrayList<>();
        for (Document d : store.values()) if (d.getLoadId().equals(loadId)) out.add(d);
        return out;
    }

    public Document get(String docId) {
        Document d = store.get(docId);
        if (d == null) throw new IllegalArgumentException("Document not found: " + docId);
        return d;
    }
}
