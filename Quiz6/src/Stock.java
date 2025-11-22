import java.util.ArrayList;
import java.util.List;

public class Stock {
    private String symbol;
    private double price;
    private List<Observer> observers = new ArrayList<>();
    private double lastPrice = 0.0;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void setPrice(double newPrice) {
        lastPrice = price;
        this.price = newPrice;
        notifyObservers();
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(symbol, price);
        }
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
