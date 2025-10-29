package Flyweight;

public class Text {
    private String character;
    private CharacterProperties charProperties;

    public Text(String character, String font, int fontSize, String color) {
        this.character = character;
        CharacterFactory characterFactory = new CharacterFactory();
        this.charProperties = characterFactory.getCharProperties(font, fontSize, color);
    }

    public String getCharacter() {
        return character + ": " + this.charProperties.toString();
    }

}
