package kr.hhplus.be.server.application.rank;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.rank.RankCommand;
import kr.hhplus.be.server.domain.rank.RankInfo;
import kr.hhplus.be.server.domain.rank.RankService;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final OrderService orderService;
    private final RankService rankService;

    @Transactional
    public void createDailyRankAt(LocalDate date) {
        OrderCommand.DateQuery orderCommand = OrderCommand.DateQuery.of(date);
        OrderCommand.PaidProducts paidProductsCommand = orderCommand.toPaidProductsQuery(OrderStatus.PAID);
        OrderInfo.PaidProducts paidProducts = orderService.getPaidProducts(paidProductsCommand);

        RankCommand.CreateList rankCommand = createListCommand(paidProducts, date);
        rankService.createSellRank(rankCommand);
    }

    private RankCommand.CreateList createListCommand(OrderInfo.PaidProducts paidProducts, LocalDate yesterday) {
        List<RankCommand.Create> commands = paidProducts.getProducts().stream()
            .map(product -> createCommand(product, yesterday))
            .toList();

        return RankCommand.CreateList.of(commands);
    }

    private RankCommand.Create createCommand(OrderInfo.PaidProduct product, LocalDate yesterday) {
        return RankCommand.Create.of(
            product.getProductId(),
            product.getQuantity(),
            yesterday
        );
    }
}
