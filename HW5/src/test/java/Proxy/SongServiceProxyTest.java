package Proxy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SongServiceProxyTest {
    private static double millis(long nanos) { return nanos / 1_000_000.0; }

    @Test
    void testProxyIdCachingSpeedsUpSecondCall() {
        SongService service = new SongServiceProxy();

        long t0 = System.nanoTime();
        Song first = service.searchById(1);           // miss -> server (≈1000ms)
        long dt1 = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        Song second = service.searchById(1);          // hit -> cache (fast)
        long dt2 = System.nanoTime() - t1;

        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first.getId(), second.getId());

        // First call should be ≈1000ms (allow slack), second should be notably faster.
        assertTrue(millis(dt1) >= 900.0, "First call should be slow due to server latency");
        assertTrue(millis(dt2) < 300.0, "Second call should be much faster due to cache");
    }

    @Test
    void testProxyTitleCachingSpeedsUpSecondCall() {
        SongService service = new SongServiceProxy();

        long t0 = System.nanoTime();
        List<Song> first = service.searchByTitle("Shivers");   // miss -> server
        long dt1 = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        List<Song> second = service.searchByTitle("Shivers");  // hit -> cache
        long dt2 = System.nanoTime() - t1;

        assertEquals(1, first.size());
        assertEquals(1, second.size());
        assertEquals(first.get(0).getTitle(), second.get(0).getTitle());

        assertTrue(millis(dt1) >= 900.0, "First title search should be slow");
        assertTrue(millis(dt2) < 300.0, "Second title search should be fast (cached)");
    }

    @Test
    void testProxyAlbumCachingSpeedsUpSecondCall() {
        SongService service = new SongServiceProxy();

        long t0 = System.nanoTime();
        List<Song> first = service.searchByAlbum("After Hours");   // miss -> server
        long dt1 = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        List<Song> second = service.searchByAlbum("After Hours");  // hit -> cache
        long dt2 = System.nanoTime() - t1;

        assertEquals(2, first.size(), "Album 'After Hours' should return two songs");
        assertEquals(2, second.size());

        assertTrue(millis(dt1) >= 900.0, "First album search should be slow");
        assertTrue(millis(dt2) < 300.0, "Second album search should be fast (cached)");
    }

    @Test
    void testSearchByIdUnknownReturnsNull() {
        SongService service = new SongServiceProxy();
        Song s = service.searchById(9999);
        assertNull(s, "Unknown ID should return null via proxy as well");
    }

    @Test
    void testSearchByTitleUnknownReturnsEmptyList() {
        SongService service = new SongServiceProxy();
        List<Song> res = service.searchByTitle("Nonexistent Title");
        assertNotNull(res);
        assertTrue(res.isEmpty(), "Unknown title should return empty list");
    }

    @Test
    void testSearchByAlbumUnknownReturnsEmptyList() {
        SongService service = new SongServiceProxy();
        List<Song> res = service.searchByAlbum("Unknown Album");
        assertNotNull(res);
        assertTrue(res.isEmpty(), "Unknown album should return empty list");
    }
}