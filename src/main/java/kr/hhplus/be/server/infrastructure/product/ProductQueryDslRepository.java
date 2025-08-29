package kr.hhplus.be.server.infrastructure.product;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.domain.product.QProduct.product;
import static kr.hhplus.be.server.domain.stock.QStock.stock;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ProductInfo.Product> findBySellStatusIn(List<ProductSellingStatus> statuses) {
        return queryFactory.select(
                Projections.constructor(
                    ProductInfo.Product.class,
                    product.id,
                    product.name,
                    product.price,
                    stock.quantity
                )
            )
            .from(product)
            .leftJoin(stock).on(product.id.eq(stock.productId))
            .where(product.sellStatus.in(statuses))
            .fetch();
    }
}
