package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findById(Long userCouponId);
}
