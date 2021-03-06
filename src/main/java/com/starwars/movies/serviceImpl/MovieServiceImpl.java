package com.starwars.movies.serviceImpl;

import com.starwars.movies.data.DataInitialization;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.repository.CommentRepository;
import com.starwars.movies.service.MovieCharacterService;
import com.starwars.movies.service.MovieService;
import com.starwars.movies.utility.ConvertToInchesAndFeet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Implements the MovieService interface methods findMovie, find all movies and  find movie character.
 * */
@Service
public class MovieServiceImpl implements MovieService
{
    @Autowired
    private DataInitialization dataInitialization; // singleton
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    MovieCharacterService movieCharacterService;
    
    public List<Movie> findAll()
    {
        return dataInitialization.getMovies().stream().map(this::getMovieDetails).collect(Collectors.toList());
    }
    
    @Override
    public Movie findMovie(String id)
    {
        Movie movie = dataInitialization.getMovies().stream().filter(mv -> mv.getMovie_id().equals(id)).findFirst().orElse(null);
        if (movie != null)
        {
            movie = getMovieDetails(movie);
        }
        
        return movie;
    }
    
    Movie getMovieDetails(Movie movie)
    {
        movie.setComments(commentRepository.getMovieComments(movie.getMovie_id())); // retrieve movie comments
        movie.setCommentCount(commentRepository.getMovieComments(movie.getMovie_id()).size()); // retrieve comment count
        
        movie.getCharacters().forEach(character ->
                                      {
                                          character.setHeightInches(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderInches(character.getHeight()));
                                          character.setHeightFeet(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderFeet(character.getHeight()));
                                      }); // retrieve height in feet and inches
        
        return movie;
    }
    
    @Override
    public Set<MovieCharacter> findMovieCharacters(String id, String sortBy, String direction, String gender)
    {
        return movieCharacterService.getMovieChars(id, sortBy, direction, gender);
    }
    
    
}
