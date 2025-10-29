package Flyweight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterPropertiesTest {
    @Test
    void testValidCharacterProperties() {
        CharacterProperties props = new CharacterProperties("Verdana", 16, "red");
        assertEquals("Verdana", props.getFont());
        assertEquals(16, props.getFontSize());
        assertEquals("red", props.getColor());
    }

    @Test
    void testToStringFormat() {
        CharacterProperties props = new CharacterProperties("Arial", 12, "black");
        assertEquals("Arial | black | 12", props.toString());
    }

    @Test
    void testGetPropertiesString() {
        CharacterProperties props = new CharacterProperties("Calibri", 14, "blue");
        String s = props.getProperties();
        assertTrue(s.contains("font= Calibri"));
        assertTrue(s.contains("color= blue"));
        assertTrue(s.contains("size= 14"));
    }
}