package Flyweight;

public class CharacterProperties {
    private String font;
    private String color;
    private int fontSize;

    public CharacterProperties(String font, int fontSize, String color) {
        this.font = font;
        this.color = color;
        this.fontSize = fontSize;
    }

    public String getFont() {
        return font;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getColor() {
        return color;
    }

    public String getProperties(){
        return "Properties: font= " + font +  " color= " + color + " size= " + fontSize;
    }

    @Override
    public String toString(){
        return font + " | " + color + " | " + fontSize;
    }


}
