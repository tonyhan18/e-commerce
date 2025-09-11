package kr.hhplus.be.server.interfaces.products;

import org.springframework.web.bind.annotation.*;

import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/products")
@RequiredArgsConstructor
public class ProductControllerV2 {
    
    private final ProductService productService;

    @GetMapping("")
    public ApiResponse<ProductResponse.Products> getProducts(
        @RequestParam("pageSize") Long pageSize,
        @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        ProductInfo.Products products = productService.getProducts(ProductCommand.Query.of(pageSize, cursor));
        return ApiResponse.success(ProductResponse.Products.of(products));
    }
} 