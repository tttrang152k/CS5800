package Startup;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import startup.Load;
import startup.Employee;

public class LoadTest {

    private Load newLoad() {
        return new Load(
                "L-1", "C-123", "Requested",1500.00f, "TRK-ABC",
                "http://rf1.pdf", null, null,
                "123 Pickup St, LA, CA",
                "456 Delivery St, LV, NV",
                "2025-10-01",
                "2025-10-05"
        );
    }

    @Test
    public void assignEquipment_setsTrailerId() {
        Load load = newLoad();

        load.assignEquipment("TRL-77");

        assertEquals("TRL-77", load.getTrailerId());

    }

    @Test
    void updateStatus_setDeliver(){
        Load load = newLoad();

        load.updateStatus("Delivered");

        assertEquals("Delivered", load.getStatus());
    }

    @Test
    void dispatch_setsStatusDispatched() {
        Load load = newLoad();

        load.dispatch();

        assertEquals("Dispatched", load.getStatus());
    }

    @Test
    void displayLoadInfo_containsKeyFields() {
        Load load = newLoad();

        String info = load.displayLoadInfo();

        assertTrue(info.contains("Load[L-1]"));
        assertTrue(info.contains("status = Requested"));
        assertTrue(info.contains("pickupAddress=123 Pickup St"));
        assertTrue(info.contains("deliveryAddress=456 Delivery St"));
    }

    @Test
    void updateStatus_acceptsNull_currentBehavior() {
        Load load = newLoad();

        load.updateStatus(null);

        assertNull(load.getStatus());
    }

    @Test
    void getters_returnConstructorValues() {
        Load load = newLoad();

        assertEquals("L-1", load.getId());
        assertEquals("C-123", load.getReferenceNo());
    }


}
