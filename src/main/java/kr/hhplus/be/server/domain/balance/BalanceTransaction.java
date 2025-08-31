package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance_transaction", indexes = {
    @Index(name = "idx_balance_transaction_balance_id", columnList = "balance_id"),
    @Index(name = "idx_balance_transaction_type", columnList = "transactionType")
})
public class BalanceTransaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에서 자동 증가(autoincrement) 방식으로 값을 생성하도록 지정
    private Long id;

    private Long balanceId;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING) // 문자열 형태로 저장
    private BalanceTransactionType transactionType;

    @Column(name = "amount")
    private Long amount;

    @Builder
    public BalanceTransaction(Long id, Long balanceId, BalanceTransactionType transactionType, Long amount) {
        this.id = id;
        this.balanceId = balanceId;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public static BalanceTransaction ofCharge(Balance balance, Long amount) {
        return BalanceTransaction.builder()
            .balanceId(balance.getId())
            .transactionType(BalanceTransactionType.CHARGE)
            .amount(amount)
            .build();
    }

    public static BalanceTransaction ofUse(Balance balance, Long amount) {
        return BalanceTransaction.builder()
            .balanceId(balance.getId())
            .transactionType(BalanceTransactionType.USE)
            .amount(-amount)
            .build();
    }

    public static BalanceTransaction ofRefund(Balance balance, long amount) {
        return BalanceTransaction.builder()
            .balanceId(balance.getId())
            .transactionType(BalanceTransactionType.REFUND)
            .amount(amount)
            .build();
    }
}
