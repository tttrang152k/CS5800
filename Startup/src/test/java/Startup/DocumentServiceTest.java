package Startup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import startup.Document;
import startup.DocumentService;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentServiceTest {
    private DocumentService svc;

    @BeforeEach
    void setUp() {
        svc = new DocumentService();
    }

    // ------- upload -------
    @Test
    void upload_BOL_returns_id_and_stores_document() {
        String id = svc.upload("L-1", "BOL", URI.create("s3://docs/bol.pdf"));
        assertNotNull(id);
        Document d = svc.get(id);
        assertEquals("L-1", d.getLoadId());
        assertEquals("BOL", d.getType());
        assertEquals("Uploaded", d.getStatus());
    }

    @Test
    void upload_POD_returns_id() {
        String id = svc.upload("L-2", "POD", URI.create("s3://docs/pod.pdf"));
        assertNotNull(id);
        assertEquals("POD", svc.get(id).getType());
    }

    @Test
    void upload_withNullFileRef_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> svc.upload("L-3", "BOL", null));
    }

    @Test
    void upload_withInvalidType_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> svc.upload("L-4", "XLSX", URI.create("s3://docs/x.xlsx")));
    }

    // ------- list -------
    @Test
    void list_returns_only_docs_for_given_load() {
        String d1 = svc.upload("L-A", "BOL", URI.create("s3://docs/a1.pdf"));
        String d2 = svc.upload("L-A", "POD", URI.create("s3://docs/a2.pdf"));
        String d3 = svc.upload("L-B", "BOL", URI.create("s3://docs/b1.pdf"));

        List<Document> forA = svc.list("L-A");
        assertEquals(2, forA.size());
        assertTrue(forA.stream().allMatch(doc -> "L-A".equals(doc.getLoadId())));

        List<Document> forB = svc.list("L-B");
        assertEquals(1, forB.size());
        assertEquals("L-B", forB.get(0).getLoadId());
    }

    // ------- get -------
    @Test
    void get_returns_document_by_id() {
        String id = svc.upload("L-9", "BOL", URI.create("s3://docs/9.pdf"));
        Document d = svc.get(id);
        assertEquals(id, d.getId());
    }

    @Test
    void get_when_missing_throws() {
        assertThrows(IllegalArgumentException.class, () -> svc.get("DOC-NOTFOUND"));
    }

    // ------- verify -------
    @Test
    void verify_sets_status_to_verified() {
        String id = svc.upload("L-10", "POD", URI.create("s3://docs/10.pdf"));
        svc.verify(id);
        assertEquals("Verified", svc.get(id).getStatus());
    }
}