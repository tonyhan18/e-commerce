package kr.hhplus.be.server.interfaces.coupon.api;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/{userId}/coupons")
    public ApiResponse<CouponResponse.Coupons> getUserCoupons(@PathVariable Long userId) {
        CouponInfo.Coupons result = couponService.getUserCoupons(userId);
        return ApiResponse.success(CouponResponse.Coupons.of(result));
    }

    @PostMapping("/{userId}/coupons/publish")
    public ApiResponse<Void> publishCoupon(@PathVariable Long userId, 
                                            @Valid @RequestBody CouponRequest.Publish request) {
        couponService.requestPublishUserCoupon(request.toCommand(userId));
        return ApiResponse.success();
    }

    
    
    
}
