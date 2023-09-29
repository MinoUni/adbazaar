package com.adbazaar.model;

import com.adbazaar.dto.comment.NewComment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser author;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

    @Builder.Default
    private Integer rate = 0;

    @Builder.Default
    private Integer likes = 0;

    @Builder.Default
    private Integer dislikes = 0;

    public static Comment build(NewComment details, AppUser author, Product product) {
        return Comment.builder()
                .author(author)
                .product(product)
                .message(details.getMessage())
                .rate(details.getRate())
                .build();
    }
}
