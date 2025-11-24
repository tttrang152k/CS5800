package com.startup.trucking.service;

import com.startup.trucking.domain.Document;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentServiceTest {

    @Test
    void test_upload_success_and_get() {
        DocumentService svc = new DocumentService();
        String id = svc.upload("L-1", "BOL", URI.create("https://example.com/bol.pdf"));
        assertNotNull(id);

        Document d = svc.get(id);
        assertEquals("L-1", d.getLoadId());
        assertEquals("BOL", d.getType());
        assertEquals("Uploaded", d.getStatus());
        assertEquals(URI.create("https://example.com/bol.pdf"), d.downloadUri());
        assertNotNull(d.getUploadedAt());
    }

    @Test
    void test_upload_rejects_null_fileRef() {
        DocumentService svc = new DocumentService();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.upload("L-1", "BOL", null));
        assertTrue(ex.getMessage().toLowerCase().contains("fileref"));
    }

    @Test
    void test_upload_rejects_non_allowed_type() {
        DocumentService svc = new DocumentService();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.upload("L-1", "OTHER", URI.create("https://x")));
        assertTrue(ex.getMessage().contains("Only BOL/POD"));
    }

    @Test
    void test_list_filters_by_loadId() {
        DocumentService svc = new DocumentService();
        String a = svc.upload("L-1", "BOL", URI.create("https://a"));
        String b = svc.upload("L-2", "POD", URI.create("https://b"));
        String c = svc.upload("L-1", "POD", URI.create("https://c"));

        List<Document> l1 = svc.list("L-1");
        assertEquals(2, l1.size());
        assertTrue(l1.stream().allMatch(d -> d.getLoadId().equals("L-1")));

        List<Document> l2 = svc.list("L-2");
        assertEquals(1, l2.size());
        assertEquals("L-2", l2.get(0).getLoadId());

        assertNotNull(svc.get(a));
        assertNotNull(svc.get(b));
        assertNotNull(svc.get(c));
    }

    @Test
    void test_get_throws_when_missing() {
        DocumentService svc = new DocumentService();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.get("DOC-nope"));
        assertTrue(ex.getMessage().contains("Document not found"));
    }
}
