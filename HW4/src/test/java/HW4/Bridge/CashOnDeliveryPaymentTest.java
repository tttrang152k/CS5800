package HW4.Bridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CashOnDeliveryPaymentTest {
    @Test
    void notifyCustomer_BitcoinPaymentNotification_ViaChannel() {
        HelperChannel channel =  new HelperChannel();
        PaymentNotification noti = new CashOnDeliveryPayment(channel, "Anna", "OR-4284");
        noti.notifyCustomer();

        assertEquals("Anna", channel.getRecipient());
        assertEquals("[CASH] Payment on delivery for order OR-4284 sent!", channel.getMessage());

    }
}