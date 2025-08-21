package kr.hhplus.be.server.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponUsedStatus;
import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
    List<UserCoupon> findByUserIdAndUsedStatusIn(Long userId, List<UserCouponUsedStatus> statuses);
    int countByCouponId(Long couponId);
    List<UserCoupon> findByCouponId(Long couponId);
}