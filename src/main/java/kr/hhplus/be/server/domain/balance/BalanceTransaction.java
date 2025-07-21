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

import lombok.Getter;
import lombok.Builder;
@Getter
@Entity
public class BalanceTransaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에서 자동 증가(autoincrement) 방식으로 값을 생성하도록 지정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩(Lazy Loading) 방식으로 설정
    @JoinColumn(name = "balance_id") // 외래 키 매핑 // 외래 키 매핑
    private Balance balance;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING) // 문자열 형태로 저장
    private BalanceTransactionType transactionType;

    @Column(name = "amount")
    private Long amount;

    @Builder
    public BalanceTransaction(Long id,Balance balance, BalanceTransactionType transactionType, Long amount) {
        this.id = id;
        this.balance = balance;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public static BalanceTransaction ofCharge(Balance balance, Long amount) {
        return BalanceTransaction.builder()
            .balance(balance)
            .transactionType(BalanceTransactionType.CHARGE)
            .amount(amount)
            .build();
    }

    public static BalanceTransaction ofUse(Balance balance, Long amount) {
        return BalanceTransaction.builder()
            .balance(balance)
            .transactionType(BalanceTransactionType.USE)
            .amount(amount)
            .build();
    }
}
