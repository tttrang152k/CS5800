package Proxy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SongTest {
    @Test
    void testSearchByIdValid() {
        Song server = new Song(); // initializes playlist
        Song s = server.searchById(9);
        assertNotNull(s, "Expected to find song with ID 9");
        assertEquals(9, s.getId());
        assertEquals("Shivers", s.getTitle());
        assertEquals("Ed Sheeran", s.getArtist());
        assertEquals("=", s.getAlbum());
        assertTrue(s.getDuration() > 0);
    }

    @Test
    void testSearchByIdInvalidReturnsNull() {
        Song server = new Song();
        Song s = server.searchById(999);
        assertNull(s, "Unknown ID should return null");
    }

    @Test
    void testSearchByTitleExactMatch() {
        Song server = new Song();
        List<Song> results = server.searchByTitle("Levitating");
        assertEquals(1, results.size(), "Expected one exact match for title 'Levitating'");
        assertEquals("Levitating", results.get(0).getTitle());
        assertEquals("Dua Lipa", results.get(0).getArtist());
        assertEquals("Future Nostalgia", results.get(0).getAlbum());
    }

    @Test
    void testSearchByAlbumReturnsMultiple() {
        Song server = new Song();
        List<Song> results = server.searchByAlbum("After Hours");
        assertEquals(2, results.size(), "Album 'After Hours' should have 2 songs");
        assertTrue(results.stream().anyMatch(s -> s.getTitle().equals("Blinding Lights")));
        assertTrue(results.stream().anyMatch(s -> s.getTitle().equals("Save Your Tears")));
    }

    @Test
    void testToStringFormat() {
        Song s = new Song(42, "Test Title", "Artist X", "Album Y", 5);
        String out = s.toString();
        assertTrue(out.contains("\"Test Title\""), "toString should include title in quotes");
        assertTrue(out.contains("by: Artist X"), "toString should include artist");
        assertTrue(out.contains("(album: Album Y)"), "toString should include album");
        assertTrue(out.contains("5 minutes"), "toString should include duration in minutes");
    }

}