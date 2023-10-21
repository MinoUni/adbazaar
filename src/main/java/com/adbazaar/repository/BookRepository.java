package com.adbazaar.repository;

import com.adbazaar.dto.book.BookDetails;
import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.dto.book.UserBook;
import com.adbazaar.dto.comment.BookComment;
import com.adbazaar.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("""
            select (count(b) > 0)
            from Book b
            where b.title = :title and b.author = :author
             and b.format = :format and b.genre = :genre
             and b.language = :language and b.publishHouse = :publishHouse""")
    boolean existsByParams(@Param("title") String title, @Param("author") String author, @Param("format") String format,
                           @Param("genre") String genre, @Param("language") String language, @Param("publishHouse") String publishHouse);

    @Query("select new com.adbazaar.dto.book.BookShortDetails(b.id, b.imagePath, b.title, b.author, b.genre, b.format, b.rate, b.quantity, b.price) from Book b")
    Page<BookShortDetails> findAllBy(Pageable pageable);

    @Query("select new com.adbazaar.dto.book.UserBook(b.id, b.author, b.title, b.imagePath, b.rate, b.price) from Book b inner join b.seller u where u.id = :id")
    List<UserBook> findAllUserBooks(@Param("id") Long id);

    @Query("""
            SELECT new com.adbazaar.dto.book.UserBook(b.id, b.author, b.title, b.imagePath, b.rate, b.price)
            FROM Book b
            INNER JOIN b.favorites u
            WHERE u.id = :id""")
    Set<UserBook> findAllUserFavoriteBooks(@Param("id") Long id);

    @Query("""
            SELECT new com.adbazaar.dto.book.UserBook(b.id, b.author, b.title, b.imagePath, b.rate, b.price)
            FROM Book b
            INNER JOIN b.buyers u
            WHERE u.id = :id""")
    Set<UserBook> findAllUserOrderedBooks(@Param("id") Long id);

    @Query("""
            SELECT new com.adbazaar.dto.book.BookDetails(b.id, b.title, b.author, b.description, b.imagePath, b.format, b.rate, b.quantity, b.price, b.genre, b.publishHouse, b.language,
            new com.adbazaar.dto.book.SellerDetails(u.id, u.fullName, u.email, u.phone, u.creationDate))
            FROM Book b
            INNER JOIN b.seller u
            WHERE b.id = :id""")
    Optional<BookDetails> findBookDetailsById(@Param("id") Long id);

    @Query("""
            SELECT new com.adbazaar.dto.comment.BookComment(c.id, u.fullName, c.creationDate, c.message, c.rate, c.likes, c.dislikes)
            FROM Book b
            INNER JOIN b.comments c
            INNER JOIN c.author u
            WHERE b.id = :id""")
    List<BookComment> findAllBookComments(@Param("id") Long id);
}
