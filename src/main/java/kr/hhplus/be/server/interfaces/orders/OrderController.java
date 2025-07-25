package kr.hhplus.be.server.interfaces.orders;

import org.springframework.web.bind.annotation.*;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.interfaces.ApiResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;

    @PostMapping("")
    public ApiResponse<Void> orderPayment(@Valid @RequestBody OrderRequest.OrderPayment request) {
        orderFacade.orderPayment(request.toCriteria()); 
        return ApiResponse.success();
    }
} 