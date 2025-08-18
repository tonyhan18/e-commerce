package kr.hhplus.be.server.interfaces.products;

import org.springframework.web.bind.annotation.*;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductFacade productFacade;

    @GetMapping("")
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductResult.Products products = productFacade.getProducts();
        return ApiResponse.success(ProductResponse.Products.of(products));
    }
} 