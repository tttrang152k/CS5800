package Startup;

import org.junit.jupiter.api.Test;
import startup.TrackingEvent;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class TrackingEventTest {
    @Test
    void getters_return_constructor_values() {
        OffsetDateTime occurred = OffsetDateTime.of(2025, 10, 28, 9, 15, 0, 0, ZoneOffset.UTC);

        TrackingEvent event = new TrackingEvent(
                occurred,
                "Loaded",
                "Los Angeles, CA"
        );

        assertEquals(occurred, event.getOccurredAt());
        assertEquals("Loaded", event.getStatus());
        assertEquals("Los Angeles, CA", event.getLocation());
    }

    @Test
    void constructor_accepts_blank_strings_and_getters_reflect_values() {
        TrackingEvent event = new TrackingEvent(
                OffsetDateTime.now(),
                "",     // blank status allowed (current behavior)
                ""      // blank location allowed (current behavior)
        );

        assertEquals("", event.getStatus());
        assertEquals("", event.getLocation());
        assertNotNull(event.getOccurredAt()); // we passed a non-null timestamp
    }
}