package kr.hhplus.be.server.infrastructure.coupon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponStatus;



public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findByIdWithLock(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);

}