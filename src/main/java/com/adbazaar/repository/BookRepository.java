package com.adbazaar.repository;

import com.adbazaar.dto.book.BookShortDetails;
import com.adbazaar.dto.book.UserBook;
import com.adbazaar.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    boolean existsByTitleAndAuthor(String title, String author);

    @Transactional(readOnly = true)
    @Query("select new com.adbazaar.dto.book.BookShortDetails(b.id, b.imagePath, b.title, b.author, b.genre, b.format, b.rate, b.quantity, b.price) from Book b")
    Page<BookShortDetails> findAllBy(Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select new com.adbazaar.dto.book.UserBook(b.id, b.author, b.title, b.imagePath, b.rate, b.price) from Book b inner join b.seller u where u.id = :id")
    List<UserBook> findAllUserBooks(@Param("id") Long id);

}
