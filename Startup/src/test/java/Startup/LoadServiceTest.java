package Startup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import startup.TrackingEvent;
import startup.EventNotify;
import startup.Load;
import startup.LoadService;
import startup.TrackingInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LoadServiceTest {
    private EventNotify notify;
    private LoadService service;

    @BeforeEach
    void setUp() {
        notify = mock(EventNotify.class);
        service = new LoadService(notify);
    }

    private Load newLoad(String id, String status) {
        return new Load(
                id,
                "REF-123",
                status,
                1500.00f,
                "TRK-ABC",
                "http://rc.pdf",
                null,           // driverId
                null,           // trailerId
                "123 Pickup St, LA, CA",
                "456 Delivery St, LV, NV",
                "2025-10-01",
                "2025-10-05"
        );
    }

    @Test
    void putLoad_then_getLoad_returnsSameInstance() {
        Load l = newLoad("L-1", "Requested");
        service.putLoad(l);

        Load found = service.getLoad("L-1");

        assertSame(l, found);
        assertEquals("Requested", found.getStatus());
    }

    @Test
    void getLoad_whenMissing_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getLoad("NOPE"));
    }

    @Test
    void getTracking_withoutEvents_returnsNADefaultLocation_andReflectsLoadStatus() {
        Load l = newLoad("L-2", "Assigned");
        service.putLoad(l);

        TrackingInfo info = service.getTracking("L-2");

        assertEquals("Assigned", info.getStatus());
        assertNotNull(info.getEta());
        assertEquals("N/A", info.getLastLocation());
        assertNotNull(info.getLastUpdated());
    }

    @Test
    void updateStatus_updatesLoad_addsEvent_andNotifies() {
        Load l = newLoad("L-3", "Requested");
        service.putLoad(l);

        service.updateStatus("L-3", "Assigned");

        // Load updated
        assertEquals("Assigned", service.getLoad("L-3").getStatus());

        // One tracking event with the same status
        List<TrackingEvent> ev = service.listTrackingEvents("L-3");
        assertEquals(1, ev.size());
        assertEquals("Assigned", ev.get(0).getStatus());
        assertEquals("Auto", ev.get(0).getLocation());

        // Notifier called with expected audience
        verify(notify).notifyLoadStatusChanged(eq("L-3"), eq("stakeholders:L-3"));
    }

    @Test
    void listTrackingEvents_returnsChronologicallySortedEvents() throws InterruptedException {
        Load l = newLoad("L-4", "Requested");
        service.putLoad(l);

        // Two status updates create two events
        service.updateStatus("L-4", "Assigned");
        Thread.sleep(5); // ensure occurredAt differs slightly (not strictly necessary but helps on fast systems)
        service.updateStatus("L-4", "Dispatched");

        List<TrackingEvent> events = service.listTrackingEvents("L-4");

        assertEquals(2, events.size());
        // First is earlier or equal to second (monotonic)
        assertTrue(!events.get(0).getOccurredAt().isAfter(events.get(1).getOccurredAt()));
        assertEquals("Assigned", events.get(0).getStatus());
        assertEquals("Dispatched", events.get(1).getStatus());
    }

    @Test
    void getTracking_withEvents_usesLastEventLocation() {
        Load l = newLoad("L-5", "Requested");
        service.putLoad(l);

        service.updateStatus("L-5", "Assigned");
        service.updateStatus("L-5", "Dispatched");

        TrackingInfo info = service.getTracking("L-5");

        assertEquals("Dispatched", info.getStatus()); // reflects current load status
        assertEquals("Auto", info.getLastLocation()); // last event location per service implementation
    }
}