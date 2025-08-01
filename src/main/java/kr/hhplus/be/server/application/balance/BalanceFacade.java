package kr.hhplus.be.server.application.balance;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.balance.BalanceInfo;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceFacade {
    private final UserService userService;
    private final BalanceService balanceService;

    @Transactional
    public void chargeBalance(BalanceCriteria.Charge criteria) {
        userService.getUser(criteria.getUserId());
        balanceService.chargeBalance(criteria.toCommand());
    }

    @Transactional(readOnly = true)
    public BalanceResult.Balance getBalance(Long userId) {
        userService.getUser(userId);
        BalanceInfo.Balance balance = balanceService.getBalance(userId);
        return BalanceResult.Balance.of(balance.getBalance());
    }
}
