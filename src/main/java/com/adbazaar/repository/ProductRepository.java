package com.adbazaar.repository;

import com.adbazaar.dto.product.ProductShortDetails;
import com.adbazaar.dto.product.UserProduct;
import com.adbazaar.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select new com.adbazaar.dto.product.ProductShortDetails(p.id, p.imagePath, p.title, p.author, p.format, p.rate, p.quantity, p.price) from Product p")
    Page<ProductShortDetails> findAllBy(Pageable pageable);

    @Query("select new com.adbazaar.dto.product.UserProduct(p.id, p.author, p.title, p.imagePath, p.rate, p.price) from Product p inner join p.seller u where u.id = :id")
    List<UserProduct> findAllUserProducts(@Param("id") Long id);
}
