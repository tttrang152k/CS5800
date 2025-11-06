public class SquareAdatper implements Payable{
    private Square square;

    public SquareAdatper(Square square) {
        this.square = square;
    }

    @Override
    public void pay(double amount){
        square.squarePayment(amount);
    }

}
