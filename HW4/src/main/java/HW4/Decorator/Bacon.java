package HW4.Decorator;

public class Bacon extends ToppingDecorator{
    public Bacon(FoodItem baseFoodItem) {
        super(baseFoodItem);
    }

    @Override
    public String description() {
        return baseFoodItem.description() + " + Bacon";
    }

    @Override
    public double price() {
        return baseFoodItem.price() + 1.75;
    }
}
