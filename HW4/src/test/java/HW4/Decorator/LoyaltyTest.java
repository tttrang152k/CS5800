package HW4.Decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoyaltyTest {
    @Test
    public void noLoyaltyandDiscount() {
        Loyalty basicTier = new Loyalty("Basic");
        double total = 20.00;
        assertEquals(total, basicTier.discount(total));
    }

    @Test
    public void friendLoyaltyandDiscount() {
        Loyalty friendTier = new Loyalty("Friend");
        double total = 20.00;
        assertEquals(19.00, friendTier.discount(total));
    }

    @Test
    public void premiumLoyaltyandDiscount2() {
        Loyalty premium = new Loyalty("Premium");
        double total = 20.00;
        assertEquals(16.00, premium.discount(total));
    }

    @Test
    public void loyalLoyaltyandDiscount3() {
        Loyalty loyal = new Loyalty("Loyalty");
        double total = 20.00;
        assertEquals(18.00, loyal.discount(total));
    }
}