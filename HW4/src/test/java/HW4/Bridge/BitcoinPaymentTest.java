package HW4.Bridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BitcoinPaymentTest {
    @Test
    void notifyCustomer_BitcoinPaymentNotification_ViaChannel() {
        HelperChannel channel =  new HelperChannel();
        PaymentNotification noti = new BitcoinPayment(channel, "Thi Thuy Trang", "T1234");
        noti.notifyCustomer();

        assertEquals("Thi Thuy Trang", channel.getRecipient());
        assertEquals("[BITCOIN] Crypto payment for transaction: T1234 sent!", channel.getMessage());

    }
}