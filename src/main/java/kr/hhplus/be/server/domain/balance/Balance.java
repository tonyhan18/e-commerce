package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Balance {

    private static final Long MAX_BALANCE_AMOUNT = 10_000_000L;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "balance")
    private Long balance;

    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    private List<BalanceTransaction> balanceTransactions = new ArrayList<>();

    @Builder
    public Balance(Long id, Long userId, Long balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        
    }

    public static Balance create(Long userId, Long balance) {
        validateBalance(balance);
        return Balance.builder()
            .userId(userId)
            .balance(balance)
            .build();
    }
    
    public void charge(Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0원 이상이어야 합니다.");
        }
        if (this.balance + amount > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 잔액 초과");
        }
        this.balance += amount;
        addChargeTransaction(amount);
    }

    public void use(Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0원 이상이어야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액 부족");
        }
        this.balance -= amount;
        addUseTransaction(amount);
    }

    private void addChargeTransaction(Long amount) {
        BalanceTransaction balanceTransaction = BalanceTransaction.ofCharge(this, amount);
        this.balanceTransactions.add(balanceTransaction);
    }
    private void addUseTransaction(Long amount) {
        BalanceTransaction balanceTransaction = BalanceTransaction.ofUse(this, amount);
        this.balanceTransactions.add(balanceTransaction);
    }
    
    
    private static void validateBalance(Long balance) {
        if(balance < 0) {
            throw new IllegalArgumentException("잔액은 0원 이상이어야 합니다.");
        }

        if(balance > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 잔액 초과");
        }
    }

}
