package kr.hhplus.be.server.interfaces.orders.api;

import org.springframework.web.bind.annotation.*;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.interfaces.ApiResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse.Order> getOrder(@PathVariable("orderId") Long orderId) {
        OrderInfo.Order order = orderService.getOrder(orderId);
        return ApiResponse.success(OrderResponse.Order.of(order));
    }

    @PostMapping("")
    public ApiResponse<OrderResponse.Order> orderPayment(@Valid @RequestBody OrderRequest.OrderPayment request) {
        OrderInfo.Order order = orderService.createOrder(request.toCommand());
        return ApiResponse.success(OrderResponse.Order.of(order));
    }
} 