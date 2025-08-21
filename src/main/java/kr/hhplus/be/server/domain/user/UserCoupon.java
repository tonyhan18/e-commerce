package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "user_coupon", 
    indexes = {
        @Index(name = "idx_user_coupon_user_id", columnList = "userId"),
        @Index(name = "idx_user_coupon_status", columnList = "usedStatus"),
        @Index(name = "idx_user_coupon_user_status", columnList = "userId,usedStatus")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_coupon_user_id_coupon_id", columnNames = {"userId", "couponId"})
    }
)
public class UserCoupon {

    @Id
    @Column(name = "user_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long couponId;

    @Enumerated(EnumType.STRING)
    private UserCouponUsedStatus usedStatus;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;



    public static UserCoupon create(Long userId, Long couponId, LocalDateTime issuedAt) {
        return UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .issuedAt(issuedAt)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();
    }

    public void use() {
        if (cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        this.usedStatus = UserCouponUsedStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public boolean cannotUse() {
        return usedStatus.cannotUsable();
    }
}
