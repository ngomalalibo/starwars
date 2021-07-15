package com.starwars.movies.controller;

import com.starwars.movies.entity.Comment;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.model.MovieCharPage;
import com.starwars.movies.model.MovieCharSearchCriteria;
import com.starwars.movies.repository.CommentRepository;
import com.starwars.movies.service.MovieCharacterService;
import com.starwars.movies.service.MovieService;
import com.starwars.movies.utility.ConvertToInchesAndFeet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Movie (comments, characters, )
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController
{
    @Autowired
    MovieCharacterService movieCharacterService;
    
    @Autowired
    CommentRepository commentRepository;
    
    @Autowired
    private MovieService movieService;
    
    /**
     * this endpoint returns the search criteria and filters by gender of the characters. Implemented in the movieService
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id, MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria)
    {
        Movie movie = movieService.findMovie(movieCharPage, movieCharSearchCriteria, id);
        return ResponseEntity.ok(movie);
    }
    
    /**
     * This endpoint adds a movie comment
     */
    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody Comment comment, @PathVariable Long id, HttpServletRequest request) // @Valid validates that comment is not more than 500 characters
    {
        if (id != null)
        {
            comment.setMovieID(id);
            LocalDateTime dateTime = LocalDateTime.now(); // get time stamp
            comment.setDateTimeUTC(dateTime.toString());
            comment.setDateTime(Timestamp.valueOf(dateTime).getTime()); // convert to milliseconds
            comment.setIdAddress(request.getRemoteAddr()); // IP address
            
            return ResponseEntity.ok(commentRepository.save(comment)); // returns the added comment
        }
        return ResponseEntity.badRequest().build();
    }
    
    /**
     * this endpoint returns the list of characters for a movie. It is able to filter by character list and provides the character height in cm, inches and feet
     */
    @GetMapping("/{id}/characters")
    public ResponseEntity<List<MovieCharacter>> getMovieCharacters(@PathVariable Long id, MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria)
    {
        List<MovieCharacter> movieChars = movieCharacterService.getMovieChars(movieCharPage, movieCharSearchCriteria, id);
        movieChars = movieChars.stream().peek(character ->
                                              {
                                                  character.setHeightInches(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderInches(character.getHeight()));
                                                  character.setHeightFeet(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderFeet(character.getHeight()));
                                              }).collect(Collectors.toList());
        return ResponseEntity.ok(movieChars);
    }
    
    /**
     * returns all movies. It includes the following details for each movie: title, opening crawls, comments, a count of comments and list of characters.
     */
    @GetMapping("/")
    public ResponseEntity<List<Movie>> getAllMovies(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria)
    {
        return ResponseEntity.ok(movieService.findAll(movieCharPage, movieCharSearchCriteria));
    }
    
    /**
     * returns a comments for a particular movie sorted in descending order. (ie most recent comments first)
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable Long id)
    {
        return ResponseEntity.ok(commentRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTime")));
    }
}
