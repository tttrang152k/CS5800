package HW4.Decorator;

public class HotDog implements FoodItem{
    @Override
    public String description() {
        return "Hot Dog";
    }

    @Override
    public double price() {
        return 4.25;
    }
}
