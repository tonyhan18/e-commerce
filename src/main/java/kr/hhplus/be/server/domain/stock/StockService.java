package kr.hhplus.be.server.domain.stock;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public StockInfo.Stock getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId);
        if (stock.isEmpty()) {
            throw new IllegalArgumentException("재고가 없습니다.");
        }
        return StockInfo.Stock.of(stock.getProductId(), stock.getQuantity());
    }

    public void deductStock(StockCommand.OrderProducts orderProducts) {
        orderProducts.getProducts().forEach(product -> {
            Stock stock = stockRepository.findByProductId(product.getProductId());
            stock.deductQuantity(product.getQuantity());
        });
    }

    public void addStock(StockCommand.OrderProducts orderProducts) {
        orderProducts.getProducts().forEach(product -> {
            Stock stock = stockRepository.findByProductId(product.getProductId());
            stock.addQuantity(product.getQuantity());
        });
    }
}
