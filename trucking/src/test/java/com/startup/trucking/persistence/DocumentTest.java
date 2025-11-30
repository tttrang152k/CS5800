package com.startup.trucking.persistence;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void test_setters_and_getters_work() {
        Document d = new Document();
        d.setId("DOC-1");
        d.setLoadId("L-1");
        d.setType("POD");
        d.setStatus("Uploaded");
        d.setFileRef("https://files.example.com/a.pdf");
        OffsetDateTime now = OffsetDateTime.now();
        d.setUploadedAt(now);

        assertEquals("DOC-1", d.getId());
        assertEquals("L-1", d.getLoadId());
        assertEquals("POD", d.getType());
        assertEquals("Uploaded", d.getStatus());
        assertEquals("https://files.example.com/a.pdf", d.getFileRef());
        assertEquals(now, d.getUploadedAt());
    }

    @Test
    void test_downloadUri_builds_from_fileRef() {
        Document d = new Document();
        d.setFileRef("https://cdn.example.com/doc.pdf");
        URI uri = d.downloadUri();

        assertNotNull(uri);
        assertEquals(URI.create("https://cdn.example.com/doc.pdf"), uri);
    }

    @Test
    void test_downloadUri_is_null_when_fileRef_null() {
        Document d = new Document();
        d.setFileRef(null);
        assertNull(d.downloadUri());
    }

    @Test
    void test_setStatus_updates_status() {
        Document d = new Document();
        d.setStatus("Uploaded");
        assertEquals("Uploaded", d.getStatus());

        d.setStatus("Verified");
        assertEquals("Verified", d.getStatus());

        d.setStatus("Rejected");
        assertEquals("Rejected", d.getStatus());
    }
}
