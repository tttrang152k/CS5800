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

    private static final Set<String> ALLOWED_TYPES = Set.of("BOL", "POD");
    private static final String STATUS_UPLOADED = "Uploaded";
    private static final String ERROR_FILE_REF_REQUIRED = "fileRef required";
    private static final String ERROR_ONLY_BOL_POD = "Only BOL/POD allowed";

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Transactional
    public String upload(String loadId, String type, URI fileRef) {
        validateUploadArguments(type, fileRef);

        String id = generateDocumentId();
        Document document = buildDocument(loadId, type, fileRef, id);

        documentRepository.save(document);
        return id;
    }

    @Transactional(readOnly = true)
    public List<Document> list(String loadId) {
        return documentRepository.findByLoadIdOrderByUploadedAtDesc(loadId);
    }

    @Transactional(readOnly = true)
    public Document get(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
    }

    @Transactional
    public void updateStatus(String documentId, String status) {
        Document document = get(documentId);
        document.setStatus(status);
        documentRepository.save(document);
    }

    @Transactional
    public void delete(String documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new IllegalArgumentException("Document not found: " + documentId);
        }
        documentRepository.deleteById(documentId);
    }

    // ---------------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------------

    private void validateUploadArguments(String type, URI fileRef) {
        if (fileRef == null) {
            throw new IllegalArgumentException(ERROR_FILE_REF_REQUIRED);
        }
        if (type == null || !ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(ERROR_ONLY_BOL_POD);
        }
    }

    private String generateDocumentId() {
        return "DOC-" + UUID.randomUUID();
    }

    private Document buildDocument(String loadId, String type, URI fileRef, String id) {
        Document document = new Document();
        document.setId(id);
        document.setLoadId(loadId);
        document.setType(type);
        document.setStatus(STATUS_UPLOADED);
        document.setFileRef(fileRef.toString());
        document.setUploadedAt(OffsetDateTime.now());
        return document;
    }
}
