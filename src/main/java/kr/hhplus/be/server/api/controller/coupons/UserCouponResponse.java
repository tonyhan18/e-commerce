package kr.hhplus.be.server.api.controller.coupons;

import java.util.List;

public class UserCouponResponse {
    private int code;
    private String message;
    private List<Data> data;

    public static class Data {
        private Long userCouponId;
        private Long couponId;
        private String name;
        private String status;
        private int discountAmount;
        private String expiredAt;

        public Data() {}
        public Data(Long userCouponId, Long couponId, String name, String status, int discountAmount, String expiredAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.name = name;
            this.status = status;
            this.discountAmount = discountAmount;
            this.expiredAt = expiredAt;
        }
        public Long getUserCouponId() { return userCouponId; }
        public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
        public Long getCouponId() { return couponId; }
        public void setCouponId(Long couponId) { this.couponId = couponId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
        public String getExpiredAt() { return expiredAt; }
        public void setExpiredAt(String expiredAt) { this.expiredAt = expiredAt; }
    }

    public UserCouponResponse() {}
    public UserCouponResponse(int code, String message, List<Data> data) {
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