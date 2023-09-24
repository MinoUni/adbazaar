package com.adbazaar.repository;

import com.adbazaar.dto.product.ProductShortDetails;
import com.adbazaar.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select new com.adbazaar.dto.product.ProductShortDetails(p.id, p.imagePath, p.title, p.author, p.format, p.rate, p.quantity, p.price) from Product p")
    Page<ProductShortDetails> findAllBy(Pageable pageable);
}
