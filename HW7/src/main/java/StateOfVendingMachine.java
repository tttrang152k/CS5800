public interface StateOfVendingMachine {
    void selectSnack(String name, int quantity);
    void insertMoney(double amount);
    void dispenseSnack();
}
