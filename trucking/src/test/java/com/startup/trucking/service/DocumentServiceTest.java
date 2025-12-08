package com.startup.trucking.service;

import com.startup.trucking.persistence.Document;
import com.startup.trucking.persistence.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Document Service Unit Tests")
class DocumentServiceTest {

    private static final String LOAD_ID = "L-1";
    private static final String DOCUMENT_ID = "DOC-1";
    private static final String BOL = "BOL";
    private static final String POD = "POD";
    private static final String FILE_URL = "https://example.com/bol.pdf";

    @Mock
    DocumentRepository documentRepository;

    // ---------------------------------------------------------------------
    // upload + get
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("upload() & get() - Successful upload persists document and can be retrieved")
    void test_upload_success_and_get() {
        DocumentService service = createService();

        when(documentRepository.save(any(Document.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String id = service.upload(LOAD_ID, BOL, URI.create(FILE_URL));
        assertNotNull(id);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepository).save(captor.capture());

        Document saved = captor.getValue();

        when(documentRepository.findById(saved.getId())).thenReturn(Optional.of(saved));

        assertEquals(LOAD_ID, saved.getLoadId());
        assertEquals(BOL, saved.getType());
        assertEquals("Uploaded", saved.getStatus());
        assertEquals(FILE_URL, saved.getFileRef());
        assertNotNull(saved.getUploadedAt());
        assertNotNull(saved.downloadUri());
        assertEquals(URI.create(FILE_URL), saved.downloadUri());

        Document got = service.get(saved.getId());
        assertSame(saved, got);
    }

    @Test
    @DisplayName("upload() - Null fileRef is rejected")
    void test_upload_rejects_null_fileRef() {
        DocumentService service = createService();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.upload(LOAD_ID, BOL, null)
        );

        assertTrue(ex.getMessage().contains("fileRef required"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    @DisplayName("upload() - Non BOL/POD type is rejected")
    void test_upload_rejects_non_allowed_type() {
        DocumentService service = createService();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.upload(LOAD_ID, "OTHER", URI.create("https://x"))
        );

        assertTrue(ex.getMessage().contains("Only BOL/POD"));
        verify(documentRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // list
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("list() - Delegates to repository and returns documents")
    void test_list_delegates_to_repository() {
        DocumentService service = createService();

        Document d1 = createDocument("DOC-1", LOAD_ID, BOL, "https://a", OffsetDateTime.now());
        Document d2 = createDocument("DOC-2", LOAD_ID, POD, "https://b",
                OffsetDateTime.now().minusMinutes(1));

        when(documentRepository.findByLoadIdOrderByUploadedAtDesc(LOAD_ID))
                .thenReturn(List.of(d1, d2));

        List<Document> documents = service.list(LOAD_ID);

        assertEquals(2, documents.size());
        assertEquals("DOC-1", documents.get(0).getId());
        verify(documentRepository).findByLoadIdOrderByUploadedAtDesc(LOAD_ID);
    }

    // ---------------------------------------------------------------------
    // get
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("get() - Existing document is returned")
    void test_get_returns_entity() {
        DocumentService service = createService();

        Document document = new Document();
        document.setId(DOCUMENT_ID);
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        Document got = service.get(DOCUMENT_ID);

        assertSame(document, got);
        verify(documentRepository).findById(DOCUMENT_ID);
    }

    @Test
    @DisplayName("get() - Missing document throws exception")
    void test_get_throws_when_missing() {
        DocumentService service = createService();
        when(documentRepository.findById("DOC-nope")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.get("DOC-nope")
        );

        assertTrue(ex.getMessage().contains("Document not found"));
        verify(documentRepository).findById("DOC-nope");
    }

    // ---------------------------------------------------------------------
    // updateStatus
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("updateStatus() - Updates status and saves document")
    void test_updateStatus_sets_status_and_saves() {
        DocumentService service = createService();

        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setStatus("Uploaded");

        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStatus(DOCUMENT_ID, "Verified");

        assertEquals("Verified", document.getStatus());
        verify(documentRepository).save(document);
    }

    // ---------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("delete() - Existing document is removed")
    void test_delete_success() {
        DocumentService service = createService();
        when(documentRepository.existsById(DOCUMENT_ID)).thenReturn(true);

        service.delete(DOCUMENT_ID);

        verify(documentRepository).deleteById(DOCUMENT_ID);
    }

    @Test
    @DisplayName("delete() - Missing document throws exception")
    void test_delete_throws_when_missing() {
        DocumentService service = createService();
        when(documentRepository.existsById("DOC-nope")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.delete("DOC-nope")
        );

        assertTrue(ex.getMessage().contains("Document not found"));
        verify(documentRepository, never()).deleteById(anyString());
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private DocumentService createService() {
        return new DocumentService(documentRepository);
    }

    private Document createDocument(String id,
                                    String loadId,
                                    String type,
                                    String fileRef,
                                    OffsetDateTime uploadedAt) {
        Document document = new Document();
        document.setId(id);
        document.setLoadId(loadId);
        document.setType(type);
        document.setStatus("Uploaded");
        document.setFileRef(fileRef);
        document.setUploadedAt(uploadedAt);
        return document;
    }
}
