package Flyweight;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class TextEditor implements Editable {
    private List<Text> text;

    public void create(List<Text> text){
        this.text = (text == null) ? new ArrayList<>() : new ArrayList<>(text);
    }

    public void edit(List<Text> text, String file){
        create(text);
        save(file);
    }

    public void save(String toFile){
        File file = new File(toFile);
        try {
            // Ensure parent directories exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (parent.mkdirs()) {
                    System.out.println("Created parent directory: " + parent.getAbsolutePath());
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Text t : this.text) {
                    writer.write(t.getCharacter());
                    writer.newLine();
                }
            }
            System.out.println("Document saved successfully to: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Failed to save to file");
            e.printStackTrace();
        }
    }

    public void load(String file){
        File f = new File(file);
        List<Text> loadedText = new ArrayList<>();

        if (!f.exists()) {
            System.out.println("File not found: " + f.getAbsolutePath());
            this.text = new ArrayList<>();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty()) continue;            // skip blanks
                if (line.startsWith("#")) continue;      // allow comment lines

                // Expected format: <char> <font> <color> <size>
                String[] parts = line.split("\\s+");
                String character = parts[0];
                String font      = parts[1];
                String color     = parts[2];
                int size = Integer.parseInt(parts[3]);
                loadedText.add(new Text(character, font, size, color));
            }
            this.text = loadedText;
            System.out.println("Loaded " + loadedText.size() + " characters from: " + f.getAbsolutePath());
            showContent();
        } catch (IOException e) {
            System.err.println("[Failed to load file: " + f.getAbsolutePath());
            e.printStackTrace();
        }
    }

    public void showContent() {
        if (this.text == null || this.text.isEmpty()) {
            System.out.println("No content loaded or created yet.");
            return;
        }

        System.out.println("=========== Current Document Content ===========");
        for (Text t : this.text) {
            System.out.println(t.getCharacter());
        }
        System.out.println("================================================");
    }
}
