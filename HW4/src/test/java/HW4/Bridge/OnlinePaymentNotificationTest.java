package HW4.Bridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OnlinePaymentNotificationTest {

    @Test
    void notifyCustomer_OnlinePaymentNotification_ViaChannel() {
        HelperChannel channel =  new HelperChannel();
        PaymentNotification noti = new OnlinePaymentNotification(channel, "Nima", "PAYPAL");
        noti.notifyCustomer();

        assertEquals("Nima", channel.getRecipient());
        assertEquals("[ONLINE] PAYPAL payment sent!", channel.getMessage());

    }

}