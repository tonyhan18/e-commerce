package kr.hhplus.be.server.api.controller.coupons;

public class CouponIssueRequest {
    private Long userId;
    private Long couponId;

    public CouponIssueRequest() {}
    public CouponIssueRequest(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
} 