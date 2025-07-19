package kr.hhplus.be.server.api.controller.rank;

import java.util.List;

public class RankTopResponse {
    private int code;
    private String message;
    private List<Data> data;

    public static class Data {
        private Long productId;
        private String name;
        private int salesCount;
        public Data() {}
        public Data(Long productId, String name, int salesCount) {
            this.productId = productId;
            this.name = name;
            this.salesCount = salesCount;
        }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getSalesCount() { return salesCount; }
        public void setSalesCount(int salesCount) { this.salesCount = salesCount; }
    }

    public RankTopResponse() {}
    public RankTopResponse(int code, String message, List<Data> data) {
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