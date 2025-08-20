package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.*;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.stock.*;
import kr.hhplus.be.server.domain.user.*;
import kr.hhplus.be.server.domain.rank.*;
import kr.hhplus.be.server.support.IntegrationTestSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFacadeIntegrationTest extends IntegrationTestSupport{

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RankRepository rankRepository;

    private User user;

    private Product product;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.builder()
            .userId(user.getId())
            .balance(500_000L)
            .build();

        balanceRepository.save(balance);

        product = Product.create("항플 블랙 뱃지", 100_000L, ProductSellingStatus.SELLING);
        productRepository.save(product);

        Stock stock = Stock.create(product.getId(), 100);
        stockRepository.save(stock);
    }

    @DisplayName("쿠폰 없이 주문 결제를 한다.")
    @Test
    void orderPaymentWithoutCoupon() {
        // given
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(user.getId(),
            List.of(OrderCriteria.OrderProduct.of(product.getId(), 2)), null
        );

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        Balance balance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(300_000L);

        Stock stock = stockRepository.findByProductId(product.getId());
        assertThat(stock.getQuantity()).isEqualTo(98);

        Order order = orderRepository.findById(result.getOrderId());
        assertThat(order.getTotalPrice()).isEqualTo(200_000L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);

        RankCommand.Query command = RankCommand.Query.of(
            1,
            RankKey.ofDays(RankType.SELL, 3),
            RankKeys.ofDaysWithDate(RankType.SELL, 3, LocalDate.now())
        );
        List<RankInfo.PopularProduct> popularSellRanks = rankRepository.findPopularSellRanks(command);
        assertThat(popularSellRanks).hasSize(1)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(product.getId());
    }

    @DisplayName("쿠폰이 있는 주문 결제를 한다.")
    @Test
    void orderPaymentWithCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        userCouponRepository.save(UserCoupon.create(user.getId(), coupon.getId()));

        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(user.getId(),
            List.of(OrderCriteria.OrderProduct.of(product.getId(), 2)), coupon.getId()
        );

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        Balance balance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(320_000L);

        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());
        assertThat(userCoupon.getUsedAt()).isNotNull();
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);

        Stock stock = stockRepository.findByProductId(product.getId());
        assertThat(stock.getQuantity()).isEqualTo(98);

        Order order = orderRepository.findById(result.getOrderId());
        assertThat(order.getTotalPrice()).isEqualTo(180_000L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);

        RankCommand.Query command = RankCommand.Query.of(
            1,
            RankKey.ofDays(RankType.SELL, 3),
            RankKeys.ofDaysWithDate(RankType.SELL, 3, LocalDate.now())
        );
        List<RankInfo.PopularProduct> popularSellRanks = rankRepository.findPopularSellRanks(command);
        assertThat(popularSellRanks).hasSize(1)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(product.getId());
    }
} 