package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "balance", indexes = {
    @Index(name = "idx_balance_user_id", columnList = "userId")
})
public class Balance {

    private static final Long MAX_BALANCE_AMOUNT = 10_000_000L;
    private static final Long INIT_BALANCE_AMOUNT = 0L;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "balance")
    private Long balance;

    // BalanceTransaction은 별도로 관리하므로 관계 매핑 제거
    // @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    // private List<BalanceTransaction> balanceTransactions = new ArrayList<>();



    public static Balance create(Long userId) {
        //validateBalance(balance);
        return Balance.builder()
            .userId(userId)
            .balance(INIT_BALANCE_AMOUNT)   
            .build();
    }
    
    public void charge(Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        if (this.balance + amount > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 금액을 초과할 수 없습니다.");
        }
        this.balance += amount;
        //addChargeTransaction(amount);
    }

    public void use(Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
        //addUseTransaction(amount);
    }

    // private void addChargeTransaction(Long amount) {
    //     BalanceTransaction balanceTransaction = BalanceTransaction.ofCharge(this, amount);
    //     this.balanceTransactions.add(balanceTransaction);
    // }
    // private void addUseTransaction(Long amount) {
    //     BalanceTransaction balanceTransaction = BalanceTransaction.ofUse(this, amount);
    //     this.balanceTransactions.add(balanceTransaction);
    // }
    
    
    // private static void validateBalance(Long balance) {
    //     if(balance < 0) {
    //         throw new IllegalArgumentException("잔액은 0원 이상이어야 합니다.");
    //     }

    //     if(balance > MAX_BALANCE_AMOUNT) {
    //         throw new IllegalArgumentException("최대 잔액 초과");
    //     }
    // }

}
