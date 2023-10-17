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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Transactional(readOnly = true)
    @Query("select new com.adbazaar.dto.book.BookShortDetails(b.id, b.imagePath, b.title, b.author, b.genre, b.format, b.rate, b.quantity, b.price) from Book b")
    Page<BookShortDetails> findAllBy(Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select new com.adbazaar.dto.book.BookDetails(b.id, b.title, b.author, b.description, b.imagePath, b.format, b.rate, b.quantity, b.price, b.genre, b.publishHouse, b.language, new com.adbazaar.dto.book.SellerDetails(u.id, u.fullName, u.email, u.phone, u.creationDate)) from Book b inner join b.seller u where b.id = :id")
    Optional<BookDetails> findBookById(Long id);

    @Transactional(readOnly = true)
    @Query("select new com.adbazaar.dto.book.UserBook(b.id, b.author, b.title, b.imagePath, b.rate, b.price) from Book b inner join b.seller u where u.id = :id")
    List<UserBook> findAllUserBooks(@Param("id") Long id);

    @Transactional(readOnly = true)
    @Query("select new com.adbazaar.dto.comment.BookComment(c.id, u.fullName, c.creationDate, c.message, c.rate, c.likes, c.dislikes) from Book b inner join b.comments c inner join c.author u where b.id = :id")
    List<BookComment> findAllBookComments(@Param("id") Long id);

}
