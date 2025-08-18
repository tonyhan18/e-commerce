package kr.hhplus.be.server.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.application.balance.BalanceFacade;
import kr.hhplus.be.server.interfaces.balance.BalanceController;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.interfaces.orders.OrderController;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.interfaces.products.ProductController;

import kr.hhplus.be.server.application.user.UserCouponFacade;
import kr.hhplus.be.server.interfaces.user.UserCouponController;

@WebMvcTest(controllers = {
    BalanceController.class,
    UserCouponController.class,
    OrderController.class,
    ProductController.class,
})
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BalanceFacade balanceFacade;

    @MockitoBean
    protected OrderFacade orderFacade;

    @MockitoBean
    protected ProductFacade productFacade;

    @MockitoBean
    protected UserCouponFacade userCouponFacade;
}
