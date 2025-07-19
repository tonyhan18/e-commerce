package kr.hhplus.be.server.api.controller.coupons;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CouponController {
    @GetMapping("/api/v1/users/{userId}/coupons")
    public ResponseEntity<UserCouponResponse> getUserCoupons(@PathVariable Long userId) {
        if (userId == 404) {
            return ResponseEntity.status(404).body(new UserCouponResponse(404, "Not Found", null));
        }
        UserCouponResponse.Data coupon = new UserCouponResponse.Data(
                701L, 501L, "가입환영할인", "AVAILABLE", 5000, "2025-06-30T23:59:59"
        );
        UserCouponResponse response = new UserCouponResponse(200, "OK", List.of(coupon));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/coupons/issue")
    public ResponseEntity<CouponIssueResponse> issueCoupon(@RequestBody CouponIssueRequest request) {
        Long userId = request.getUserId();
        Long couponId = request.getCouponId();
        if (userId == 404 || couponId == 404) {
            return ResponseEntity.status(404).body(new CouponIssueResponse(404, "Not Found", null));
        }
        if (couponId == 409) {
            return ResponseEntity.status(409).body(new CouponIssueResponse(409, "이미 발급/선착순 소진/만료 등", null));
        }
        CouponIssueResponse.Data data = new CouponIssueResponse.Data(702L, couponId, "AVAILABLE");
        CouponIssueResponse response = new CouponIssueResponse(200, "쿠폰이 발급되었습니다.", data);
        return ResponseEntity.ok(response);
    }
} 