package kr.hhplus.be.server.domain.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockInfo {
    @Getter
    public static class Stock {
        private final Long productId;
        private final int quantity;

        private Stock(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static Stock of(Long productId, int quantity) {
            return new Stock(productId, quantity);
        }
    }
}
