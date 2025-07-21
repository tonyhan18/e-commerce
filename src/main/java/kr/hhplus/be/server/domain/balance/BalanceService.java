package kr.hhplus.be.server.domain.balance;

import org.springframework.stereotype.Service;import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private static final Long EMPTY_BALANCE_AMOUNT = 0L;

    public BalanceInfo.Balance getBalance(Long userId) {
        return balanceRepository.findOptionalByUserId(userId)
            .map(Balance::getBalance)
            .map(BalanceInfo.Balance::of)
            .orElse(BalanceInfo.Balance.of(EMPTY_BALANCE_AMOUNT));
    }

    public void chargeBalance(BalanceCommand.Charge command) {
        balanceRepository.findOptionalByUserId(command.getUserId())
        .ifPresentOrElse(
            balance -> balance.charge(command.getAmount()),
            () -> {
                Balance newBalance = Balance.create(command.getUserId(), command.getAmount());
                balanceRepository.save(newBalance);
            }
        );
    }

    public void useBalance(BalanceCommand.Use command) {
        balanceRepository.findOptionalByUserId(command.getUserId())
        .ifPresentOrElse(
            balance -> balance.use(command.getAmount()),
            () -> {
                Balance newBalance = Balance.create(command.getUserId(), command.getAmount());
                balanceRepository.save(newBalance);
            }
        );
    }
}
