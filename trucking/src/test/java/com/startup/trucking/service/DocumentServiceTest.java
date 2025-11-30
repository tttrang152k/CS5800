package com.startup.trucking.service;

import com.startup.trucking.persistence.Document;
import com.startup.trucking.persistence.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    DocumentRepository repo;

    @Test
    void test_upload_success_and_get() {
        DocumentService svc = new DocumentService(repo);

        when(repo.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

        String id = svc.upload("L-1", "BOL", URI.create("https://example.com/bol.pdf"));
        assertNotNull(id);

        // Capture what was saved
        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(repo).save(captor.capture());
        Document saved = captor.getValue();

        when(repo.findById(saved.getId())).thenReturn(Optional.of(saved));

        // Assert saved fields
        assertEquals("L-1", saved.getLoadId());
        assertEquals("BOL", saved.getType());
        assertEquals("Uploaded", saved.getStatus());
        assertEquals("https://example.com/bol.pdf", saved.getFileRef());
        assertNotNull(saved.getUploadedAt());
        assertNotNull(saved.downloadUri());
        assertEquals(URI.create("https://example.com/bol.pdf"), saved.downloadUri());

        // Assert get()
        Document got = svc.get(saved.getId());
        assertSame(saved, got);
    }

    @Test
    void test_upload_rejects_null_fileRef() {
        DocumentService svc = new DocumentService(repo);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.upload("L-1", "BOL", null));
        assertTrue(ex.getMessage().toLowerCase().contains("fileref"));
        verify(repo, never()).save(any());
    }

    @Test
    void test_upload_rejects_non_allowed_type() {
        DocumentService svc = new DocumentService(repo);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.upload("L-1", "OTHER", URI.create("https://x")));
        assertTrue(ex.getMessage().contains("Only BOL/POD"));
        verify(repo, never()).save(any());
    }

    @Test
    void test_list_delegates_to_repository() {
        DocumentService svc = new DocumentService(repo);

        Document d1 = new Document();
        d1.setId("DOC-1");
        d1.setLoadId("L-1");
        d1.setType("BOL");
        d1.setStatus("Uploaded");
        d1.setFileRef("https://a");
        d1.setUploadedAt(OffsetDateTime.now());

        Document d2 = new Document();
        d2.setId("DOC-2");
        d2.setLoadId("L-1");
        d2.setType("POD");
        d2.setStatus("Uploaded");
        d2.setFileRef("https://b");
        d2.setUploadedAt(OffsetDateTime.now().minusMinutes(1));

        when(repo.findByLoadIdOrderByUploadedAtDesc("L-1")).thenReturn(List.of(d1, d2));

        List<Document> docs = svc.list("L-1");
        assertEquals(2, docs.size());
        assertEquals("DOC-1", docs.get(0).getId()); // order preserved from repository
        verify(repo, times(1)).findByLoadIdOrderByUploadedAtDesc("L-1");
    }

    @Test
    void test_get_returns_entity() {
        DocumentService svc = new DocumentService(repo);

        Document d = new Document();
        d.setId("DOC-xyz");
        when(repo.findById("DOC-xyz")).thenReturn(Optional.of(d));

        Document got = svc.get("DOC-xyz");
        assertSame(d, got);
        verify(repo).findById("DOC-xyz");
    }

    @Test
    void test_get_throws_when_missing() {
        DocumentService svc = new DocumentService(repo);
        when(repo.findById("DOC-nope")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.get("DOC-nope"));
        assertTrue(ex.getMessage().contains("Document not found"));
        verify(repo).findById("DOC-nope");
    }

    @Test
    void test_updateStatus_sets_status_and_saves() {
        DocumentService svc = new DocumentService(repo);

        Document d = new Document();
        d.setId("DOC-1");
        d.setStatus("Uploaded");
        when(repo.findById("DOC-1")).thenReturn(Optional.of(d));
        when(repo.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

        svc.updateStatus("DOC-1", "Verified");

        assertEquals("Verified", d.getStatus());
        verify(repo).save(d);
    }

    @Test
    void test_delete_success() {
        DocumentService svc = new DocumentService(repo);
        when(repo.existsById("DOC-1")).thenReturn(true);

        svc.delete("DOC-1");

        verify(repo).deleteById("DOC-1");
    }

    @Test
    void test_delete_throws_when_missing() {
        DocumentService svc = new DocumentService(repo);
        when(repo.existsById("DOC-nope")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.delete("DOC-nope"));
        assertTrue(ex.getMessage().contains("Document not found"));
        verify(repo, never()).deleteById(anyString());
    }
}
