package Proxy;

import java.util.List;

public interface SongService {
    public Song searchById(Integer songID);
    public List<Song> searchByTitle(String title);
    public List<Song> searchByAlbum(String album);
}
