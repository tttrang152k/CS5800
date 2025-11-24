package com.startup.trucking.domain;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void test_constructor_setsFields_andUploadedAtIsRecent() {
        OffsetDateTime before = OffsetDateTime.now().minusSeconds(2);
        var doc = new Document("DOC-1", "L-1", "POD", "Uploaded", URI.create("https://x/y.pdf"));
        OffsetDateTime after = OffsetDateTime.now().plusSeconds(2);

        assertEquals("DOC-1", doc.getId());
        assertEquals("L-1", doc.getLoadId());
        assertEquals("POD", doc.getType());
        assertEquals("Uploaded", doc.getStatus());
        assertEquals(URI.create("https://x/y.pdf"), doc.downloadUri());

        assertNotNull(doc.getUploadedAt());
        assertFalse(doc.getUploadedAt().isBefore(before));
        assertFalse(doc.getUploadedAt().isAfter(after));
        assertTrue(Duration.between(before, doc.getUploadedAt()).getSeconds() < 10);
    }

    @Test
    void test_downloadUri_returnsProvidedUri() {
        var uri = URI.create("https://files.example.com/d.pdf");
        var doc = new Document("DOC-2", "L-2", "BOL", "Uploaded", uri);
        assertEquals(uri, doc.downloadUri());
    }

    @Test
    void test_getters_returnValues() {
        var uri = URI.create("https://files/a.pdf");
        var doc = new Document("DOC-3", "L-3", "POD", "Verified", uri);

        assertEquals("DOC-3", doc.getId());
        assertEquals("L-3", doc.getLoadId());
        assertEquals("POD", doc.getType());
        assertEquals("Verified", doc.getStatus());
        assertEquals(uri, doc.downloadUri());
        assertNotNull(doc.getUploadedAt());
    }

    @Test
    void test_setStatus_updatesStatus() {
        var doc = new Document("DOC-4", "L-4", "BOL", "Uploaded", URI.create("https://f"));
        doc.setStatus("Verified");
        assertEquals("Verified", doc.getStatus());
        doc.setStatus("Rejected");
        assertEquals("Rejected", doc.getStatus());
    }
}
