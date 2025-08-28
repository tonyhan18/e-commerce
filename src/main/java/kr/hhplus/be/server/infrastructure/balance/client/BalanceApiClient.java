package kr.hhplus.be.server.infrastructure.balance.client;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.balance.BalanceClient;
import kr.hhplus.be.server.domain.balance.BalanceInfo;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceApiClient implements BalanceClient {

    @Override
    public BalanceInfo.User getUser(Long userId) {
        return null;
    }
}
