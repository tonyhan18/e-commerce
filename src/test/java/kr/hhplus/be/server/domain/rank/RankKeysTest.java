package kr.hhplus.be.server.domain.rank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RankKeysTest {

    @DisplayName("일수와 날짜로 키들을 생성한다.")
    @Test
    void ofDaysWithDate() {
        // given
        int days = 3;
        LocalDate date = LocalDate.of(2025, 8, 19);

        // when
        RankKeys rankKeys = RankKeys.ofDaysWithDate(RankType.SELL, days, date);

        // then
        assertThat(rankKeys.getFirstKey()).isEqualTo("rank:sell:20250819");
        assertThat(rankKeys.getOtherKeys()).hasSize(3)
            .containsExactly(
                "rank:sell:20250818",
                "rank:sell:20250817",
                "rank:sell:20250816"
            );
    }

}