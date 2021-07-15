package com.starwars.movies.repository;

import com.starwars.movies.entity.MovieCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieCharRepository extends JpaRepository<MovieCharacter, Long>
{
    @Query(value = "select * from movies_charaters mc where mc.movi_id=:id ORDER BY :sortBy :direction", nativeQuery = true)
    public List<MovieCharacter> getMovieChars(@Param("id") long id, @Param("sortBy") String sortBy, @Param("direction") String direction);
}
