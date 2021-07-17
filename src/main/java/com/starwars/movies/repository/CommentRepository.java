package com.starwars.movies.repository;

import com.starwars.movies.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comments repository
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>
{
    // returns all comments for a particular movie ordered by date with the most recent comments first.
    @Query(value = "select * from comments c where c.movie_id=:id ORDER BY c.date_time DESC", nativeQuery = true)
    List<Comment> getMovieComments(@Param("id") String id);
}
