package com.adbazaar.model;

import com.adbazaar.dto.book.NewBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
import org.hibernate.proxy.HibernateProxy;

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
    private Long id;

    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    private String format;

    @Builder.Default
    @Column(columnDefinition = "Decimal(5, 2)")
    private Double rate = 0.0;

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

    public static Book build(NewBook details, String imageUrl, AppUser user) {
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
                .imagePath(imageUrl)
                .build();
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Book book = (Book) object;
        return getId() != null && Objects.equals(getId(), book.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
