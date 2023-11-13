package com.adbazaar.model;

import com.adbazaar.dto.comment.NewComment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser author;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Builder.Default
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String message;

    @Builder.Default
    @Column(columnDefinition = "Decimal(5, 2)")
    private Double rate = 0.0;

    @Builder.Default
    private Integer likes = 0;

    @Builder.Default
    private Integer dislikes = 0;

    public static Comment build(NewComment details, AppUser author, Book book) {
        return Comment.builder()
                .author(author)
                .book(book)
                .message(details.getMessage())
                .rate(Math.round(details.getRate() * 2.0) / 2.0)
                .build();
    }
}
