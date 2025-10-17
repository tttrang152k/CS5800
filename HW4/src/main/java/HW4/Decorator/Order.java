package HW4.Decorator;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<FoodItem> foodItems;

    public Order() {
        this.foodItems = new ArrayList<>();
    }

    public Order(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    public void addFoodItem(FoodItem foodItem) {
        this.foodItems.add(foodItem);
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public double subTotal() {
        double total = 0;
        for (FoodItem item : foodItems) {
            total += item.price();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ======= Customer Order ======= \n");
        for (FoodItem item : foodItems) {
            sb.append("Food: " + item.description() + " : $" + item.price() + "\n");
        }
        sb.append("=> Subtotal: $" + subTotal());
        return sb.toString();
    }
}
