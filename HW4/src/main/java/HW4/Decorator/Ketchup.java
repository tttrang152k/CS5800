package HW4.Decorator;

public class Ketchup extends ToppingDecorator{
    public Ketchup(FoodItem baseFoodItem) {
        super(baseFoodItem);
    }

    @Override
    public String description() {
        return baseFoodItem.description() + " + Ketchup";
    }

    @Override
    public double price() {
        return baseFoodItem.price() + 0.25;
    }
}
