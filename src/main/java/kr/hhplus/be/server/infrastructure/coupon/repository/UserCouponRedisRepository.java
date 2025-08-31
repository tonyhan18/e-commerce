package kr.hhplus.be.server.infrastructure.coupon.repository;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponKey;
import kr.hhplus.be.server.domain.user.UserCouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserCouponRedisRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public boolean save(CouponCommand.PublishRequest command) {
        CouponKey key = CouponKey.of(command.getCouponId());
        long score = command.getIssuedAt().toEpochSecond(ZoneOffset.UTC);

        return Boolean.TRUE.equals(redisTemplate.opsForZSet().addIfAbsent(key.generate(), command.getUserId(), score));
    }

    public List<UserCouponInfo.Candidates> findPublishCandidates(CouponCommand.Candidates command) {
        CouponKey key = CouponKey.of(command.getCouponId());

        Set<TypedTuple<Long>> tuples = redisTemplate.opsForZSet().rangeWithScores(key.generate(), command.getStart(), command.getEnd() - 1);

        return Optional.ofNullable(tuples)
            .map(this::getList)
            .orElse(new ArrayList<>());
    }

    private List<UserCouponInfo.Candidates> getList(Set<TypedTuple<Long>> set) {
        return set.stream()
            .map(this::toUserCoupon)
            .toList();
    }

    private UserCouponInfo.Candidates toUserCoupon(TypedTuple<Long> longTypedTuple) {
        Long userId = longTypedTuple.getValue();
        LocalDateTime issuedAt = Optional.ofNullable(longTypedTuple.getScore())
            .map(Double::longValue)
            .map(this::toLocalDateTime)
            .orElse(LocalDateTime.now());

        return UserCouponInfo.Candidates.of(userId, issuedAt);
    }

    private LocalDateTime toLocalDateTime(Long l) {
        return LocalDateTime.ofEpochSecond(l, 0, ZoneOffset.UTC);
    }
}
