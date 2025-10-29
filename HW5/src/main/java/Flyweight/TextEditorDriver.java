package Flyweight;

import java.util.ArrayList;
import java.util.List;

public class TextEditorDriver {
    private static final String[] fonts = {"Arial", "Calibri", "Verdana"};
    private static final int[] fontSizes = {12, 14, 16};
    private static final String[] colors = {"red", "blue", "black"};

    public static List<Text>  randomCharPropertiesGenerator(String text){
        List<Text> texts = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            String randFont = fonts[(int) (Math.random() * fontSizes.length)];
            int randFontSize = fontSizes[(int) (Math.random() * fontSizes.length)];
            String randColor = colors[(int) (Math.random() * colors.length)];
            texts.add(new Text(text.substring(i, i + 1), randFont, randFontSize, randColor));
        }
        return texts;
    }

    public static void main(String[] args) {
        // Simulator: create text in text editor then save to file
        String filePath = "HW5/src/main/java/Flyweight/output.txt";
        TextEditor editor = new TextEditor();
        List<Text> generatedText = randomCharPropertiesGenerator("HelloWorldCS5800");
        editor.create(generatedText);
        editor.save(filePath);

        // edit
        String filePath2 = "HW5/src/main/java/Flyweight/editOutput.txt";
        List<Text> anotherText = randomCharPropertiesGenerator("HelloWorldCS5800");
        editor.edit(anotherText, filePath2);

        // load
        String filePath3 = "HW5/src/main/java/Flyweight/input.txt";
        editor.load(filePath3);
    }
}
