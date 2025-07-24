package kr.hhplus.be.server.interfaces.user;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.user.UserCouponCriteria;
import kr.hhplus.be.server.application.user.UserCouponFacade;
import kr.hhplus.be.server.application.user.UserCouponResult;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponFacade userCouponFacade;

    @GetMapping("/{userId}/coupons")
    public ApiResponse<UserCouponResponse.Coupons> getUserCoupons(@PathVariable Long userId) {
        UserCouponResult.Coupons result = userCouponFacade.getUserCoupons(userId);
        return ApiResponse.success(UserCouponResponse.Coupons.of(result));
    }

    @PostMapping("/{userId}/coupons/publish")
    public ApiResponse<Void> publishCoupon(@PathVariable Long userId, @Valid @RequestBody UserCouponRequest.Publish request) {
        UserCouponCriteria.Publish command = request.toCriteria(userId);
        userCouponFacade.publishUserCoupon(command);
        return ApiResponse.success();
    }
    
    
}
