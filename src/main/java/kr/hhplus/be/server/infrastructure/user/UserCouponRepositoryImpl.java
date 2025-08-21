package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    
    private final UserCouponJpaRepository userCouponJpaRepository;
    private final UserCouponRedisRepository userCouponRedisRepository;
    private final UserCouponJdbcTemplateRepository userCouponJdbcTemplateRepository;

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public UserCoupon findByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId)
            .orElseThrow(() -> new IllegalArgumentException("UserCoupon not found with userId: " + userId + " and couponId: " + couponId));
    }

    @Override
    public List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses) {
        return userCouponJpaRepository.findByUserIdAndUsedStatusIn(userId, statuses);
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
            .orElseThrow(() -> new IllegalArgumentException("UserCoupon not found with id: " + userCouponId));
    }

    @Override
    public boolean save(UserCouponCommand.PublishRequest command) {
        return userCouponRedisRepository.save(command);
    }

    @Override
    public int countByCouponId(Long couponId) {
        return userCouponJpaRepository.countByCouponId(couponId);
    }

    @Override
    public List<UserCouponInfo.Candidates> findPublishCandidates(UserCouponCommand.Candidates command) {
        return userCouponRedisRepository.findPublishCandidates(command);
    }

    @Override
    public void saveAll(List<UserCoupon> userCoupons) {
        userCouponJdbcTemplateRepository.batchInsert(userCoupons);
    }

    @Override
    public List<UserCoupon> findCouponId(Long couponId) {
        return userCouponJpaRepository.findByCouponId(couponId);
    }
} 