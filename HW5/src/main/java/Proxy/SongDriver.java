package Proxy;

import java.util.List;

public class SongDriver {
    public static void main(String[] args) {
        SongService songService = new SongServiceProxy();

        System.out.println("Search by ID ===============");
        long startTime = System.currentTimeMillis();
        System.out.println(songService.searchById(3));
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        System.out.println(songService.searchById(3));
        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");


        System.out.println("Search by Title ===============");
        startTime = System.currentTimeMillis();
        List<Song> songsByTitle = songService.searchByTitle("Stay");
        endTime = System.currentTimeMillis();
        songsByTitle.forEach(System.out::println);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        List<Song> songsByTitle2 = songService.searchByTitle("Stay");
        endTime = System.currentTimeMillis();
        songsByTitle2.forEach(System.out::println);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        System.out.println("Search by Artist ===============");
        startTime = System.currentTimeMillis();
        List<Song> songsByAlb = songService.searchByAlbum("After Hours");
        endTime = System.currentTimeMillis();
        songsByAlb.forEach(System.out::println);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        List<Song> songsByAlb2 = songService.searchByAlbum("After Hours");
        endTime = System.currentTimeMillis();
        songsByAlb2.forEach(System.out::println);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

    }
}
