package HW4.Decorator;

public class Fries implements FoodItem {
    @Override
    public String description() {
        return "Fries";
    }

    @Override
    public double price() {
        return 5.5;
    }
}
