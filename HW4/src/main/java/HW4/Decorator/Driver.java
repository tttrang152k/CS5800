package HW4.Decorator;

public class Driver {
    public static void main(String[] args) {
        // Create foods
        FoodItem stuffedBurger = new Cheese(new Onion(new Bacon(new Burger())));
        // Create an order and add item
        Order order1 = new Order();
        order1.addFoodItem(stuffedBurger);
        // apply loyalty discount
        Loyalty friendTier = new Loyalty("Friend");
        double discount = friendTier.discount(order1.subTotal());
        System.out.println(order1);
        System.out.println("Loyalty status: " + friendTier.getStatus() + ". Subtotal after discount: $" + discount);
        System.out.println();

        FoodItem regHotdog = new Onion(new Ketchup(new HotDog()));
        FoodItem animalFries = new Cheese(new Onion(new Bacon(new Fries())));
        Order order2 = new Order();
        order2.addFoodItem(regHotdog);
        order2.addFoodItem(animalFries);
        Loyalty basicTier = new Loyalty("Basic");   // none discount
        double discount2 = basicTier.discount(order2.subTotal());
        System.out.println(order2);
        System.out.println("Loyalty status: " + basicTier.getStatus() + ". Subtotal after discount: $" + discount2);
        System.out.println();

        FoodItem burger = new Onion(new Ketchup(new Burger()));
        FoodItem regFries = new Cheese(new Fries());
        Order order3 = new Order();
        order3.addFoodItem(burger);
        order3.addFoodItem(regFries);
        Loyalty premiumTier = new Loyalty("Premium");   // none discount
        double discount3 = premiumTier.discount(order3.subTotal());
        System.out.println(order3);
        System.out.println("Loyalty status: " + premiumTier.getStatus() + ". Subtotal after discount: $" + discount3);

    }
}
