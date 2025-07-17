package kr.hhplus.be.server.api.controller.products;

import java.util.List;

public class ProductListResponse {
    private int code;
    private String message;
    private List<Data> data;

    public static class Data {
        private Long productId;
        private String name;
        private int price;
        private String status;
        private int stock;
        public Data() {}
        public Data(Long productId, String name, int price, String status, int stock) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.status = status;
            this.stock = stock;
        }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    public ProductListResponse() {}
    public ProductListResponse(int code, String message, List<Data> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<Data> getData() { return data; }
    public void setData(List<Data> data) { this.data = data; }
} 