package com.starwars.movies.service;

import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.model.MovieCharPage;
import com.starwars.movies.model.MovieCharSearchCriteria;

import java.util.List;

public interface MovieService
{
    List<Movie> findAll(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria);
    
    Movie findMovie(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, long id);
    
    List<MovieCharacter> findMovieCharacters(long id);
}
