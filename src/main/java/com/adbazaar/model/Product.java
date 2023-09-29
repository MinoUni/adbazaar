package com.adbazaar.model;

import com.adbazaar.dto.product.CreateProductReq;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"seller", "comments"})
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long id;

    private String title;

    private String author;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    private String format;

    @Column(precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal rate = BigDecimal.valueOf(0);

    @Builder.Default
    private Integer quantity = 0;

    @Column(precision = 8, scale = 2)
    private BigDecimal price;

    private String genre;

    private String language;

    @Column(name = "publishing_house")
    private String publishHouse;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser seller;

    @Builder.Default
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    public static Product build(CreateProductReq details, AppUser user) {
        return Product.builder()
                .title(details.getTitle())
                .author(details.getAuthor())
                .genre(details.getGenre())
                .description(details.getDescription())
                .format(details.getFormat())
                .price(details.getPrice())
                .quantity(details.getQuantity())
                .language(details.getLanguage())
                .publishHouse(details.getPublishHouse())
                .seller(user)
                .imagePath(details.getImagePath())
                .build();
    }

}
