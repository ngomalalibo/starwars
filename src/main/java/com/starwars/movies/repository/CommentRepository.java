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
    @Query(value = "select * from movies m, movie_comments  where mc.movie_id=m.id and m.id=:id", nativeQuery = true)
    List<Comment> getMovieComments(@Param("id") long id);
}
