package kr.hhplus.be.server.interfaces.products;

import org.springframework.web.bind.annotation.*;

import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @GetMapping("")
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ApiResponse.success(ProductResponse.Products.of(products));
    }
} 