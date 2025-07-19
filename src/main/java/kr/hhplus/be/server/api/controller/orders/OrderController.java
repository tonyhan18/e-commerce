package kr.hhplus.be.server.api.controller.orders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @PostMapping("")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Long userId = request.getUserId();
        if (userId == 404) {
            return ResponseEntity.status(404).body(new OrderResponse(404, "Not Found", null));
        }
        if (userId == 400) {
            return ResponseEntity.badRequest().body(new OrderResponse(400, "잔액/재고/쿠폰 불가 등", null));
        }
        OrderResponse.Data.Item item1 = new OrderResponse.Data.Item(1001L, 2, 4500);
        OrderResponse.Data.Item item2 = new OrderResponse.Data.Item(1002L, 1, 4800);
        OrderResponse.Data data = new OrderResponse.Data(301L, "PAID", 13800, 8800, 5000, List.of(item1, item2));
        OrderResponse response = new OrderResponse(200, "주문 및 결제가 완료되었습니다.", data);
        return ResponseEntity.ok(response);
    }
} 