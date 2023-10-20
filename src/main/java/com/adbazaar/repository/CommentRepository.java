package com.adbazaar.repository;

import com.adbazaar.dto.comment.UserBookComment;
import com.adbazaar.dto.comment.UserComment;
import com.adbazaar.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select new com.adbazaar.dto.comment.UserBookComment(c.id, b.id, u.fullName, c.creationDate, c.message, c.rate, c.likes, c.dislikes)
            from Comment c
            inner join c.author u
            inner join c.book b
            where u.id = :id""")
    List<UserBookComment> findAllByUserId(@Param("id") Long id);

    @Query("""
            select new com.adbazaar.dto.comment.UserComment(c.id, c.creationDate, c.message, new com.adbazaar.dto.comment.BookInComment(b.id, b.title, b.author, b.imagePath))
            from Comment c
            inner join c.author u
            inner join c.book b
            where u.id = :id""")
    List<UserComment> findAllUserComments(@Param("id") Long id);
}
