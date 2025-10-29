package Proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongServiceProxy implements SongService {
    private SongService songService;
    private Map<Integer, Song> idCache;
    private Map<String, List<Song>> titleCache;
    private Map<String, List<Song>> albumCache;

    public SongServiceProxy() {
        this.songService = new Song();
        this.idCache = new HashMap<>();
        this.titleCache = new HashMap<>();
        this.albumCache = new HashMap<>();
    }

    @Override
    public Song searchById(Integer songID){
        if (idCache.containsKey(songID)){
            System.out.println("Cache hit for song ID: " + songID);
            return idCache.get(songID);
        }
        System.out.println("Cache miss for song ID: " + songID);
        System.out.println("Searching on real server for song ID: " + songID);
        Song song = songService.searchById(songID);
        if  (song != null){
            idCache.put(songID, song);
        }
        return song;
    }

    @Override
    public List<Song> searchByTitle(String title){
        if  (titleCache.containsKey(title)){
            System.out.println("Cache hit for song title: " + title);
            return titleCache.get(title);
        }
        System.out.println("Cache miss for song title: " + title);
        System.out.println("Searching on real server for song title: " + title);
        List<Song> songs = songService.searchByTitle(title);
        if  (songs != null){
            titleCache.put(title, songs);
        }
        return songs;
    }

    @Override
    public List<Song> searchByAlbum(String album){
        if (albumCache.containsKey(album)){
            System.out.println("Cache hit for song album: " + album);
            return albumCache.get(album);
        }
        System.out.println("Cache miss for song album: " + album);
        System.out.println("Searching on real server for song album: " + album);
        List<Song> songs = songService.searchByAlbum(album);
        if  (songs != null){
            albumCache.put(album, songs);
        }
        return songs;
    }
}
