package kr.hhplus.be.server.api.controller.products;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts() {
        ProductListResponse.Data p1 = new ProductListResponse.Data(1001L, "아메리카노", 4500, "SELLABLE", 50);
        ProductListResponse.Data p2 = new ProductListResponse.Data(1002L, "카페라떼", 4800, "SELLABLE", 20);
        ProductListResponse response = new ProductListResponse(200, "OK", List.of(p1, p2));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top")
    public ResponseEntity<ProductTopResponse> getTopProducts() {
        ProductTopResponse.Data t1 = new ProductTopResponse.Data(1001L, "아메리카노", 257);
        ProductTopResponse.Data t2 = new ProductTopResponse.Data(1002L, "카페라떼", 110);
        ProductTopResponse response = new ProductTopResponse(200, "OK", List.of(t1, t2));
        return ResponseEntity.ok(response);
    }
} 