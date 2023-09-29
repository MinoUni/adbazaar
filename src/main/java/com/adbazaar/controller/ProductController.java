package com.adbazaar.controller;

import com.adbazaar.dto.ApiResponse;
import com.adbazaar.dto.product.CreateProductReq;
import com.adbazaar.dto.product.ProductDetails;
import com.adbazaar.dto.product.ProductShortDetails;
import com.adbazaar.dto.product.ProductUpdate;
import com.adbazaar.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Tag(name = "Product Management")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse> addNewProduct(@RequestHeader(AUTHORIZATION) String token,
                                                     @RequestBody CreateProductReq productDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productDetails, token));
    }

    @GetMapping
    public ResponseEntity<Page<ProductShortDetails>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetails> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.deleteById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateById(@PathVariable("id") Long id,
                                                  @RequestBody ProductUpdate details) {
        return ResponseEntity.ok(productService.update(id, details));
    }
}
