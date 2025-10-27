package Startup;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import startup.TrackingInfo;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

class TrackingInfoTest {

    @Test
    void getters_return_constructor_values() {
        OffsetDateTime eta = OffsetDateTime.of(2025, 10, 30, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime lastUpdated = OffsetDateTime.of(2025, 10, 29, 8, 30, 0, 0, ZoneOffset.UTC);

        TrackingInfo info = new TrackingInfo(
                "EnRoute",
                eta,
                "Bakersfield, CA",
                lastUpdated
        );

        assertEquals("EnRoute", info.getStatus());
        assertEquals(eta, info.getEta());
        assertEquals("Bakersfield, CA", info.getLastLocation());
        assertEquals(lastUpdated, info.getLastUpdated());
    }

    @Test
    void constructor_allows_null_optional_fields_and_getters_reflect_nulls() {
        // Class has no validation — document current behavior:
        TrackingInfo info = new TrackingInfo(
                null,   // status
                null,   // eta
                null,   // lastLocation
                null    // lastUpdated
        );

        assertNull(info.getStatus());
        assertNull(info.getEta());
        assertNull(info.getLastLocation());
        assertNull(info.getLastUpdated());
    }
}