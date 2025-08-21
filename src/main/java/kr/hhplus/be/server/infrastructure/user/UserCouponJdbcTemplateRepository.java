package kr.hhplus.be.server.infrastructure.user;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import kr.hhplus.be.server.domain.user.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCouponJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<UserCoupon> userCoupons) {
        String sql = "INSERT INTO user_coupon (user_id, coupon_id, used_status, issued_at) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserCoupon userCoupon = userCoupons.get(i);
                ps.setLong(1, userCoupon.getUserId());
                ps.setLong(2, userCoupon.getCouponId());
                ps.setString(3, userCoupon.getUsedStatus().name());
                ps.setTimestamp(4, Timestamp.valueOf(userCoupon.getIssuedAt()));
            }

            @Override
            public int getBatchSize() {
                return userCoupons.size();
            }
        });
    }
}
