package com.starwars.movies.service;

import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;

import java.util.List;

public interface MovieService
{
    List<Movie> findAll();
    
    Movie findMovie(String id);
    
    List<MovieCharacter> findMovieCharacters(String id, String sortBy, String direction, String gender);
}
