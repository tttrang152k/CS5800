package HW4.Decorator;

public abstract class ToppingDecorator implements FoodItem {
    protected FoodItem baseFoodItem;

    public ToppingDecorator(FoodItem baseFoodItem) {
        this.baseFoodItem = baseFoodItem;
    }

    @Override
    public String description() {
        return baseFoodItem.description();
    }

    @Override
    public double price() {
        return baseFoodItem.price();
    }

}
