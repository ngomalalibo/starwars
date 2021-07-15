package com.starwars.movies.serviceImpl;

import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.model.MovieCharPage;
import com.starwars.movies.model.MovieCharSearchCriteria;
import com.starwars.movies.repository.CommentRepository;
import com.starwars.movies.repository.MovieRepository;
import com.starwars.movies.service.MovieCharacterService;
import com.starwars.movies.service.MovieService;
import com.starwars.movies.utility.ConvertToInchesAndFeet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService
{
    @Autowired
    MovieRepository movieRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    MovieCharacterService movieCharacterService;
    
    public List<Movie> findAll(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria)
    {
        List<Movie> movies = movieRepository.findAll(Sort.by(Sort.Direction.ASC, "release_date"));
        return movies.stream().map(movie ->
                                           getMovieDetails(movieCharPage, movieCharSearchCriteria, movie)).collect(Collectors.toList());
    }
    
    @Override
    public Movie findMovie(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, long id)
    {
        Movie movie = movieRepository.findById(id).orElse(new Movie());
        
        movie = getMovieDetails(movieCharPage, movieCharSearchCriteria, movie);
        
        return movie;
    }
    
    Movie getMovieDetails(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, Movie movie)
    {
        movie.setComments(commentRepository.getMovieComments(movie.getId()));
        movie.setCommentCount(commentRepository.getMovieComments(movie.getId()).size());
        
        List<MovieCharacter> movieChars = movieCharacterService.getMovieChars(movieCharPage, movieCharSearchCriteria, movie.getId());
        movieChars = movieChars.stream().peek(character ->
                                              {
                                                  character.setHeightInches(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderInches(character.getHeight()));
                                                  character.setHeightFeet(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderFeet(character.getHeight()));
                                              }).collect(Collectors.toList());
        
        movie.setCharacters(movieChars);
        
        return movie;
    }
    
    @Override
    public List<MovieCharacter> findMovieCharacters(long id)
    {
        return null;
    }
}
