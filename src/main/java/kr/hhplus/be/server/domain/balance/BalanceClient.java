package kr.hhplus.be.server.domain.balance;

public interface BalanceClient {

    BalanceInfo.User getUser(Long userId);
}
