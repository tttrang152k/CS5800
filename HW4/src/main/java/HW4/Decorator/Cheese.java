package HW4.Decorator;

public class Cheese extends ToppingDecorator{
    public Cheese(FoodItem baseFoodItem) {
        super(baseFoodItem);
    }

    @Override
    public String description() {
        return baseFoodItem.description() + " + Cheese";
    }

    @Override
    public double price() {
        return baseFoodItem.price() + 0.75;
    }
}
