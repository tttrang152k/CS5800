package HW4.Decorator;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class OrderTest {

    @Test
    public void orderItemNumber() {
        Order order = new Order();
        FoodItem item1 = new Cheese(new Burger());
        FoodItem item2 = new Onion(new Fries());
        FoodItem item3 = new Ketchup(new HotDog());

        order.addFoodItem(item1);
        order.addFoodItem(item2);
        order.addFoodItem(item3);

        List<FoodItem> items = order.getFoodItems();
        assertEquals(3, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
        assertTrue(items.contains(item3));
    }

    @Test
    public void orderSubtotal() {
        Order order = new Order();
        FoodItem item1 = new Cheese(new Burger());  // $6.75
        FoodItem item2 = new Onion(new Fries());    // $5.9

        order.addFoodItem(item1);
        order.addFoodItem(item2);

        assertEquals(12.65, order.subTotal());

    }

    @Test
    public void orderToppingDescription(){
        Order order = new Order();
        FoodItem item1 = new Cheese(new Ketchup(new Burger()));
        FoodItem item2 = new Onion(new Fries());

        order.addFoodItem(item1);
        order.addFoodItem(item2);

        String orderDescription = order.toString();

        assertTrue(orderDescription.contains("Burger + Ketchup + Cheese"));
        assertTrue(orderDescription.contains("Fries + Onions"));
    }
}