package kr.hhplus.be.server.domain.balance;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private static final Long EMPTY_BALANCE_AMOUNT = 0L;

    public BalanceInfo.Balance getBalance(Long userId) {
        Long balance = balanceRepository.findOptionalByUserId(userId)
            .map(Balance::getBalance)
            .orElse(EMPTY_BALANCE_AMOUNT);
        return BalanceInfo.Balance.of(balance);
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
