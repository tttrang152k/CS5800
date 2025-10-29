package Flyweight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextTest {
    @Test
    void testGetCharacterCombinesCharacterAndProperties() {
        Text t = new Text("H", "Verdana", 16, "red");
        // Expect format: "<char>: <font> | <color> | <size>"
        assertEquals("H: Verdana | red | 16", t.getCharacter());
    }
}