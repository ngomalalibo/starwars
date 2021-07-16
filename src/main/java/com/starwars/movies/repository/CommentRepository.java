package com.starwars.movies.repository;

import com.starwars.movies.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>
{
    @Query(value = "select * from comments c where c.movie_id=:id", nativeQuery = true)
    List<Comment> getMovieComments(@Param("id") String id);
}
