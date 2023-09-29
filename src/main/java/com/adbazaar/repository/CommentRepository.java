package com.adbazaar.repository;

import com.adbazaar.dto.comment.CommentDetails;
import com.adbazaar.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select new com.adbazaar.dto.comment.CommentDetails(c.id, p.id, u.fullName, c.creationDate, c.message, c.likes, c.dislikes)
            from Comment c
            inner join c.author u
            inner join c.product p
            where u.id = :id""")
    List<CommentDetails> findAllByUserId(@Param("id") Long id);

    @Query("""
            select new com.adbazaar.dto.comment.CommentDetails(c.id, p.id, u.fullName, c.creationDate, c.message, c.likes, c.dislikes)
            from Comment c
            inner join c.author u
            inner join c.product p
            where p.id = :id""")
    List<CommentDetails> findAllByProductId(@Param("id") Long id);
}
