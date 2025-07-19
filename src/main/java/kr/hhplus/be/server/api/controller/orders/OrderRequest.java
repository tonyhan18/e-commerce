package kr.hhplus.be.server.api.controller.orders;

import java.util.List;

public class OrderRequest {
    private Long userId;
    private List<Item> items;
    private Long userCouponId;

    public static class Item {
        private Long productId;
        private int quantity;
        public Item() {}
        public Item(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public OrderRequest() {}
    public OrderRequest(Long userId, List<Item> items, Long userCouponId) {
        this.userId = userId;
        this.items = items;
        this.userCouponId = userCouponId;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public Long getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
} 