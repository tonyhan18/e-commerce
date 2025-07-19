package kr.hhplus.be.server.api.controller.balance;

public class BalanceResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private int amount;
        public Data() {}
        public Data(int amount) { this.amount = amount; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
    }

    public BalanceResponse() {}
    public BalanceResponse(int code, String message, Data data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
} 