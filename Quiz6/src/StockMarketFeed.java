import java.util.HashMap;
import java.util.Map;

public class StockMarketFeed implements Observer{
    private final Map<String, Double> lastState = new HashMap<>();

    @Override
    public void update(String symbol, double price) {
        String status;
        Double lastPrice = lastState.get(symbol);
        if (lastPrice == null) {
            status = "| new on market";
        }
        else if (price < lastPrice) {
            status = "- price going DOWN";
        }
        else if (price > lastPrice) {
            status = "+ price going UP";
        }
        else {
            status = "= price unchanged";
        }
        lastState.put(symbol, price);
        System.out.println(symbol + " at $" + price + " " + status);
    }
}
