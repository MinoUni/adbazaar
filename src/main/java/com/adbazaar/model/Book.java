package com.adbazaar.model;

import com.adbazaar.dto.book.NewBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"seller", "comments", "favorites", "buyers"})
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @OneToMany(mappedBy = "book")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "favoriteBooks")
    private Set<AppUser> favorites = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "orders")
    private Set<AppUser> buyers = new HashSet<>();

    public static Book build(NewBook details, AppUser user) {
        return Book.builder()
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Book book = (Book) object;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) && Objects.equals(format, book.format) &&
                Objects.equals(genre, book.genre) && Objects.equals(language, book.language) &&
                Objects.equals(publishHouse, book.publishHouse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, format, genre, language, publishHouse);
    }
}
