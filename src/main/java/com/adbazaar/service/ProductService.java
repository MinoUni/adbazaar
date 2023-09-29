package com.adbazaar.service;

import com.adbazaar.dto.ApiResp;
import com.adbazaar.dto.product.CreateProductReq;
import com.adbazaar.dto.product.ProductDetails;
import com.adbazaar.dto.product.ProductShortDetails;
import com.adbazaar.dto.product.ProductUpdate;
import com.adbazaar.exception.ProductNotFoundException;
import com.adbazaar.exception.UserNotFoundException;
import com.adbazaar.model.Product;
import com.adbazaar.repository.ProductRepository;
import com.adbazaar.repository.UserRepository;
import com.adbazaar.security.JwtService;
import com.adbazaar.utils.CustomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CustomMapper mapper;
    private final JwtService jwtService;

    public ApiResp create(CreateProductReq productDetails, String token) {
        var email = jwtService.extractUsernameFromAccessToken(token.substring(7));
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email {%s} not found", email)));
        var product = Product.build(productDetails, user);
        productRepo.save(product);
        return ApiResp.builder()
                .status(HttpStatus.CREATED.value())
                .message("Product created")
                .build();
    }

    @Transactional(readOnly = true)
    public ProductDetails findById(Long id) {
        return productRepo.findById(id)
                .map(ProductDetails::build)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id {%d} not found", id)));
    }

    @Transactional(readOnly = true)
    public Page<ProductShortDetails> findAll(Pageable pageable) {
        return productRepo.findAllBy(pageable);
    }

    public ApiResp deleteById(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ProductNotFoundException(String.format("Product with id {%d} not found", id));
        }
        productRepo.deleteById(id);
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message(String.format("Product with id {%d} deleted", id))
                .build();
    }

    public ApiResp update(Long id, ProductUpdate details) {
        var product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id {%d} not found", id)));
        mapper.updateProduct(details, product);
        productRepo.save(product);
        return ApiResp.builder()
                .status(HttpStatus.OK.value())
                .message(String.format("Product with id {%d} updated", id))
                .build();
    }


}
