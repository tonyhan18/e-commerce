package kr.hhplus.be.server.interfaces.rank.event;

import kr.hhplus.be.server.domain.order.OrderEvent;
import kr.hhplus.be.server.domain.rank.RankService;
import kr.hhplus.be.server.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class RankOrderEventListenerTest extends MockTestSupport {

    @InjectMocks
    private RankOrderEventListener rankOrderEventListener;

    @Mock
    private RankService rankService;

    @DisplayName("주문 완료 시, 랭킹 정보를 업데이트한다.")
    @Test
    void handleCompleted() {
        // given
        OrderEvent.Completed event = mock(OrderEvent.Completed.class);

        // when
        rankOrderEventListener.handle(event);

        // then
        verify(rankService, times(1)).createSellRank(any());
    }

}