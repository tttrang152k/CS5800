package HW4.Decorator;

public class Loyalty {
    private String status;

    public Loyalty(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public double discount(double subtotal){
        switch (status) {
            case "Friend":
                return subtotal * 0.95; // 5%
            case "Loyalty":
                return subtotal * 0.90; // 10%
            case "Premium":
                return subtotal * 0.80; // 20%
            default:
                return subtotal;    // no discount
        }
    }
}
