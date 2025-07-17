package kr.hhplus.be.server.api.controller.balance;

public class BalanceRequest {
    private int amount;

    public BalanceRequest() {}
    public BalanceRequest(int amount) { this.amount = amount; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
} 