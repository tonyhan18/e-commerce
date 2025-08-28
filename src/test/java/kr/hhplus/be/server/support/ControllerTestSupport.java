package kr.hhplus.be.server.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.rank.RankFacade;
import kr.hhplus.be.server.application.user.UserCouponFacade;
import kr.hhplus.be.server.interfaces.balance.api.BalanceController;
import kr.hhplus.be.server.interfaces.orders.api.OrderController;
import kr.hhplus.be.server.interfaces.products.ProductController;
import kr.hhplus.be.server.interfaces.rank.RankController;
import kr.hhplus.be.server.interfaces.user.UserCouponController;

@WebMvcTest(controllers = {
    BalanceController.class,
    UserCouponController.class,
    OrderController.class,
    ProductController.class,
    RankController.class,
})
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BalanceService balanceService;

    @MockitoBean
    protected OrderFacade orderFacade;

    @MockitoBean
    protected ProductFacade productFacade;

    @MockitoBean
    protected RankFacade rankFacade;

    @MockitoBean
    protected UserCouponFacade userCouponFacade;
}
