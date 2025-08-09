package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);    

    UserCoupon findById(Long userCouponId);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses);

    Optional<UserCoupon> findOptionalByUserIdAndCouponId(Long userId, Long couponId);

}
