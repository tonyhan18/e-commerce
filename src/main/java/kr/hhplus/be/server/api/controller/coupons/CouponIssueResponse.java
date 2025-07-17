package kr.hhplus.be.server.api.controller.coupons;

public class CouponIssueResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private Long userCouponId;
        private Long couponId;
        private String status;

        public Data() {}
        public Data(Long userCouponId, Long couponId, String status) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.status = status;
        }
        public Long getUserCouponId() { return userCouponId; }
        public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
        public Long getCouponId() { return couponId; }
        public void setCouponId(Long couponId) { this.couponId = couponId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public CouponIssueResponse() {}
    public CouponIssueResponse(int code, String message, Data data) {
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