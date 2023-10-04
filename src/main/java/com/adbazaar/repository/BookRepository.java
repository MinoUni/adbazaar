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

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select new com.adbazaar.dto.book.BookShortDetails(b.id, b.imagePath, b.title, b.author, b.format, b.rate, b.quantity, b.price) from Book b")
    Page<BookShortDetails> findAllBy(Pageable pageable);

    @Query("select new com.adbazaar.dto.book.UserBook(b.id, b.author, b.title, b.imagePath, b.rate, b.price) from Book b inner join b.seller u where u.id = :id")
    List<UserBook> findAllUserBooks(@Param("id") Long id);
}
