//package com.startup.trucking.service;
//
//import com.startup.trucking.domain.Document;
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class DocumentService {
//    private final Map<String, Document> store = new ConcurrentHashMap<>();
//    private static final Set<String> ALLOWED = Set.of("BOL", "POD");
//
//    public String upload(String loadId, String type, URI fileRef) {
//        if (fileRef == null) throw new IllegalArgumentException("fileRef required");
//        if (!ALLOWED.contains(type)) {
//            throw new IllegalArgumentException("Only BOL/POD allowed");
//        }
//        String id = "DOC-" + UUID.randomUUID();
//        store.put(id, new Document(id, loadId, type, "Uploaded", fileRef));
//        return id;
//    }
//
//    public List<Document> list(String loadId) {
//        List<Document> out = new ArrayList<>();
//        for (Document d : store.values()) if (d.getLoadId().equals(loadId)) out.add(d);
//        return out;
//    }
//
//    public Document get(String docId) {
//        Document d = store.get(docId);
//        if (d == null) throw new IllegalArgumentException("Document not found: " + docId);
//        return d;
//    }
//}
package com.startup.trucking.service;

import com.startup.trucking.persistence.Document;
import com.startup.trucking.persistence.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository repo;
    private static final Set<String> ALLOWED = Set.of("BOL", "POD");

    public DocumentService(DocumentRepository repo) { this.repo = repo; }

    @Transactional
    public String upload(String loadId, String type, URI fileRef) {
        if (fileRef == null) throw new IllegalArgumentException("fileRef required");
        if (type == null || !ALLOWED.contains(type)) throw new IllegalArgumentException("Only BOL/POD allowed");

        String id = "DOC-" + UUID.randomUUID();

        Document e = new Document();
        e.setId(id);
        e.setLoadId(loadId);
        e.setType(type);
        e.setStatus("Uploaded");
        e.setFileRef(fileRef.toString());
        e.setUploadedAt(OffsetDateTime.now());

        repo.save(e);
        return id;
    }

    @Transactional(readOnly = true)
    public List<Document> list(String loadId) {
        return repo.findByLoadIdOrderByUploadedAtDesc(loadId);
    }

    @Transactional(readOnly = true)
    public Document get(String docId) {
        return repo.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + docId));
    }

    @Transactional
    public void updateStatus(String docId, String status) {
        Document e = get(docId);
        e.setStatus(status);
        repo.save(e);
    }

    @Transactional
    public void delete(String docId) {
        if (!repo.existsById(docId)) throw new IllegalArgumentException("Document not found: " + docId);
        repo.deleteById(docId);
    }
}
