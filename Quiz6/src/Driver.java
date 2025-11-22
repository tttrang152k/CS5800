import java.util.Random;

public class Driver {
    private static final Random random = new Random();
    public static double randomPriceChanger(double currentPrice) {
        double percentChange = (random.nextDouble() - 0.5) * 0.02;
        double change = currentPrice * (1 + percentChange);
        change = Math.max(0.01, change);
        return Math.round(change * 100.0) / 100.0;
    }

    public static void main(String[] args) throws InterruptedException {
        Stock Meta = new Stock("FB", 181.36);
        Stock Amazon = new Stock("AMZ", 223.55);

        StockMarketFeed observer1 = new StockMarketFeed();
        StockMarketFeed observer2 = new StockMarketFeed();

        Meta.addObserver(observer1);
        Meta.addObserver(observer2);
        Amazon.addObserver(observer1);

        for (int i = 0; i < 5; i++){
            Meta.setPrice(randomPriceChanger(Meta.getPrice()));
            Amazon.setPrice(randomPriceChanger(Amazon.getPrice()));
            System.out.println("--------------------------------------");
            Thread.sleep(1000);
        }
    }
}
