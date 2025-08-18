package kr.hhplus.be.server.infrastructure.order;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderProduct;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;
    private final QOrderProduct orderProduct = QOrderProduct.orderProduct;

    public List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command) {
        return queryFactory.select(
                Projections.constructor(
                    OrderInfo.PaidProduct.class,
                    orderProduct.productId,
                    orderProduct.quantity.sum().as("quantity")
                )
            )
            .from(order)
            .join(order.orderProducts, orderProduct)
            .where(
                order.orderStatus.eq(command.getStatus()),
                order.paidAt.between(
                    command.getPaidAt().minusDays(1).atStartOfDay(),
                    command.getPaidAt().atStartOfDay()
                )
            )
            .groupBy(orderProduct.productId)
            .fetch();
    }
}
