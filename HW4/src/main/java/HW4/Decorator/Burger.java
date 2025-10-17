package HW4.Decorator;

public class Burger implements FoodItem {
    @Override
    public String description() {
        return "Burger";
    }

    @Override
    public double price() {
        return 6.0;
    }
}
