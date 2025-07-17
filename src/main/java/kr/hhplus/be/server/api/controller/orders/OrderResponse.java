package kr.hhplus.be.server.api.controller.orders;

import java.util.List;

public class OrderResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private Long orderId;
        private String status;
        private int totalAmount;
        private int paidAmount;
        private int discountAmount;
        private List<Item> items;

        public static class Item {
            private Long productId;
            private int quantity;
            private int price;
            public Item() {}
            public Item(Long productId, int quantity, int price) {
                this.productId = productId;
                this.quantity = quantity;
                this.price = price;
            }
            public Long getProductId() { return productId; }
            public void setProductId(Long productId) { this.productId = productId; }
            public int getQuantity() { return quantity; }
            public void setQuantity(int quantity) { this.quantity = quantity; }
            public int getPrice() { return price; }
            public void setPrice(int price) { this.price = price; }
        }

        public Data() {}
        public Data(Long orderId, String status, int totalAmount, int paidAmount, int discountAmount, List<Item> items) {
            this.orderId = orderId;
            this.status = status;
            this.totalAmount = totalAmount;
            this.paidAmount = paidAmount;
            this.discountAmount = discountAmount;
            this.items = items;
        }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getTotalAmount() { return totalAmount; }
        public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
        public int getPaidAmount() { return paidAmount; }
        public void setPaidAmount(int paidAmount) { this.paidAmount = paidAmount; }
        public int getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
        public List<Item> getItems() { return items; }
        public void setItems(List<Item> items) { this.items = items; }
    }

    public OrderResponse() {}
    public OrderResponse(int code, String message, Data data) {
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