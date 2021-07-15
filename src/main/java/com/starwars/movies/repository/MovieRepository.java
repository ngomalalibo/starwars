package com.starwars.movies.repository;

import com.starwars.movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{
    /*@Query(value = "select Count(m.id) from movies m where m.id=:id", nativeQuery = true)
    long getMovieCount(@Param("id") long id);*/
}
