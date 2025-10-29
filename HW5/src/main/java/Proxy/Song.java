package Proxy;

import java.util.ArrayList;
import java.util.List;

public class Song implements SongService {
    private int id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private static List<Song> playlist;

    public Song(){
        playlist = createSongPlaylist();
    }

    public Song(int id, String title, String artist, String album, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    @Override
    public Song searchById(Integer songID){
        sleep();
        for (Song song : playlist){
            if(song.getId() == songID){
                return song;
            }
        }
        return null;
    }

    @Override
    public List<Song> searchByTitle(String title){
        sleep();
        List<Song> found = new ArrayList<>();
        for (Song song : playlist){
            if(song.getTitle().equals(title)){
                found.add(song);
            }
        }
        return found;
    }

    @Override
    public List<Song> searchByAlbum(String album){
        sleep();
        List<Song> found = new ArrayList<>();
        for (Song song : playlist){
            if(song.getAlbum().equals(album)){
                found.add(song);
            }
        }
        return found;
    }

    private void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Song> createSongPlaylist() {
        List<Song> songs = new ArrayList<>();
        // create 5 - 10 songs
        songs.add(new Song(1, "Blinding Lights", "The Weeknd", "After Hours", 4));
        songs.add(new Song(2, "Save Your Tears", "The Weeknd", "After Hours", 4));
        songs.add(new Song(3, "Levitating", "Dua Lipa", "Future Nostalgia", 3));
        songs.add(new Song(4, "Don’t Start Now", "Dua Lipa", "Future Nostalgia", 3));
        songs.add(new Song(5, "Peaches", "Justin Bieber", "Justice", 4));
        songs.add(new Song(6, "Stay", "The Kid LAROI", "F*ck Love 3", 3));
        songs.add(new Song(7, "Drivers License", "Olivia Rodrigo", "SOUR", 4));
        songs.add(new Song(8, "Bad Habits", "Ed Sheeran", "=", 4));
        songs.add(new Song(9, "Shivers", "Ed Sheeran", "=", 3));
        songs.add(new Song(10, "Heat Waves", "Glass Animals", "Dreamland", 4));
        return songs;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getDuration() { return duration; }

    public String toString() {
        return "\"" + title + "\" by: " + artist + " (album: " + album + ") - " + duration + " minutes";
    }

}
