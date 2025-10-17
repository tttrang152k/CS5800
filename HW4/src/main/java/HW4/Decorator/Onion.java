package HW4.Decorator;

public class Onion extends ToppingDecorator{
    public Onion(FoodItem baseFoodItem) {
        super(baseFoodItem);
    }

    @Override
    public String description() {
        return baseFoodItem.description() + " + Onions";
    }

    @Override
    public double price() {
        return baseFoodItem.price() + 0.40;
    }
}
