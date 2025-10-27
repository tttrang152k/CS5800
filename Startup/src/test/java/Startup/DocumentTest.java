package Startup;

import org.junit.jupiter.api.Test;
import startup.Document;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {
    @Test
    void getters_return_constructor_values() {
        URI ref = URI.create("s3://bucket/bol.pdf");
        Document d = new Document("DOC-1", "L-1", "BOL", "Uploaded", ref);

        assertEquals("DOC-1", d.getId());
        assertEquals("L-1", d.getLoadId());
        assertEquals("BOL", d.getType());
        assertEquals("Uploaded", d.getStatus());
        assertEquals(ref, d.downloadUri());
    }

    @Test
    void setStatus_updates_status() {
        Document d = new Document("DOC-2", "L-2", "POD", "Uploaded", URI.create("file://pod.pdf"));
        d.setStatus("Verified");
        assertEquals("Verified", d.getStatus());
    }

    @Test
    void uploadedAt_is_set_in_constructor() throws Exception {
        Document d = new Document("DOC-3", "L-3", "RC", "Uploaded", URI.create("file://rc.pdf"));

        Field f = Document.class.getDeclaredField("uploadedAt");
        f.setAccessible(true);
        Object ts = f.get(d);

        assertNotNull(ts);
        assertTrue(ts instanceof OffsetDateTime);
    }

    @Test
    void downloadUri_returns_original_uri() {
        URI ref = URI.create("https://cdn.example.com/doc.pdf");
        Document d = new Document("DOC-4", "L-4", "Other", "Uploaded", ref);

        assertEquals(ref, d.downloadUri());
    }
}