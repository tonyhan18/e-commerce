package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserCouponUsedStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCouponRepositoryImpl implements UserCouponRepository {
    
    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        // TODO: 실제 DB 연동 로직 구현
        return userCoupon;
    }

    @Override
    public List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses) {
        // TODO: 실제 DB 연동 로직 구현
        return List.of();
    }

    @Override
    public UserCoupon findByUserIdAndCouponId(Long userId, Long couponId) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }
} 