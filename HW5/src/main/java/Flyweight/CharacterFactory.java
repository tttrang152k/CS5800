package Flyweight;

import java.util.HashMap;

public class CharacterFactory {
    private static final HashMap<String, CharacterProperties> charProperties = new HashMap<>(); // Use String for simplifiability

    public CharacterProperties getCharProperties(String font, int fontSize, String color) {
        String charKey = getCharKey(font, fontSize, color);
        CharacterProperties charValue = charProperties.get(charKey);

        // same character properties check
        // optimize for memory usage
        if (charValue == null) {
            charValue = new CharacterProperties(font, fontSize, color);
            charProperties.put(charKey, charValue);
        }
        return charValue;
    }

    private String getCharKey(String font, int fontSize, String color) {
        String charKeyTemp = font + " " + fontSize + " " + color;
        return charKeyTemp;
    }
}
