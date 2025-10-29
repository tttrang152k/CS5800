package Flyweight;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextEditorTest {

    private static final Path BASE_PATH = Paths.get("src/test/java/Flyweight/");

    // helper for stdout
    private static class StdoutCapture implements AutoCloseable {
        private final PrintStream original = System.out;
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();
        StdoutCapture() { System.setOut(new PrintStream(out)); }
        String output() { return out.toString(); }
        @Override public void close() { System.setOut(original); }
    }

    // helpers for create content
    private List<Text> sampleContent1() {
        List<Text> list = new ArrayList<>();
        list.add(new Text("H", "Verdana", 16, "red"));
        list.add(new Text("i", "Arial", 12, "black"));
        return list;
    }

    private List<Text> sampleContent2() {
        List<Text> list = new ArrayList<>();
        list.add(new Text("B", "Calibri", 14, "black"));
        list.add(new Text("y", "Verdana", 16, "red"));
        list.add(new Text("e", "Arial", 12, "black"));
        return list;
    }

    @Test
    void testValidFile_saveCreatesFileWithExpectedContent() throws Exception {
        TextEditor editor = new TextEditor();
        editor.create(sampleContent1());

        Path file = BASE_PATH.resolve("test_save_valid.txt");
        Files.createDirectories(file.getParent());

        editor.save(file.toString());

        assertTrue(Files.exists(file), "Expected file to be created");

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("H: Verdana | red | 16", lines.get(0));
        assertEquals("i: Arial | black | 12", lines.get(1));
    }

    @Test
    void testEditFile_overwritesExistingContent() throws Exception {
        TextEditor editor = new TextEditor();

        Path file = BASE_PATH.resolve("test_edit_overwrite.txt");
        Files.createDirectories(file.getParent());

        // Initial save
        editor.create(sampleContent1());
        editor.save(file.toString());

        List<String> first = Files.readAllLines(file);
        assertEquals(List.of("H: Verdana | red | 16", "i: Arial | black | 12"), first);

        // Edit (replace content) and persist
        editor.edit(sampleContent2(), file.toString());

        List<String> second = Files.readAllLines(file);
        assertEquals(3, second.size());
        assertEquals("B: Calibri | black | 14", second.get(0));
        assertEquals("y: Verdana | red | 16", second.get(1));
        assertEquals("e: Arial | black | 12", second.get(2));
    }

    @Test
    void testLoadFile_parsesSpaceSeparatedFormat() throws Exception {
        // Prepare input with the new space-separated format
        Path file = BASE_PATH.resolve("test_load_input.txt");
        Files.createDirectories(file.getParent());
        List<String> input = List.of(
                "H Verdana red 16",
                "e Arial black 12",
                "l Calibri black 12"
        );
        Files.write(file, input);

        TextEditor editor = new TextEditor();
        String out;
        try (StdoutCapture cap = new StdoutCapture()) {
            editor.load(file.toString());
            out = cap.output();
        }

        assertTrue(out.contains("Loaded 3 characters"), "Should report 3 characters loaded");
        assertTrue(out.contains("H: Verdana | red | 16"));
        assertTrue(out.contains("e: Arial | black | 12"));
        assertTrue(out.contains("l: Calibri | black | 12"));
    }

    @Test
    void testLoadMissingFile_showsWarningAndNoCrash() {
        TextEditor editor = new TextEditor();

        Path missing = BASE_PATH.resolve("missing_test_file.txt");

        String out;
        try (StdoutCapture cap = new StdoutCapture()) {
            editor.load(missing.toString());
            out = cap.output();
        }
        assertTrue(out.contains("File not found"), "Should warn about missing file");
    }

    @Test
    void testShowContent_whenEmptyPrintsMessage() {
        TextEditor editor = new TextEditor();
        String out;
        try (StdoutCapture cap = new StdoutCapture()) {
            editor.showContent();
            out = cap.output();
        }
        assertTrue(out.contains("No content loaded or created yet."));
    }

    @Test
    void testShowContent_printsAllCurrentLines() {
        TextEditor editor = new TextEditor();
        editor.create(sampleContent2());
        String out;
        try (StdoutCapture cap = new StdoutCapture()) {
            editor.showContent();
            out = cap.output();
        }
        assertTrue(out.contains("=========== Current Document Content ==========="));
        assertTrue(out.contains("B: Calibri | black | 14"));
        assertTrue(out.contains("y: Verdana | red | 16"));
        assertTrue(out.contains("e: Arial | black | 12"));
        assertTrue(out.contains("================================================"));
    }
}