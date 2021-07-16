package com.starwars.movies.controller;

import com.starwars.movies.entity.Comment;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.repository.CommentRepository;
import com.starwars.movies.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for Movie (comments, characters, )
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController
{
    @Autowired
    CommentRepository commentRepository;
    
    @Autowired
    private MovieService movieService;
    
    /**
     * returns all movies. It includes the following details for each movie: title, opening crawls, comments, a count of comments and list of characters.
     */
    @GetMapping({"/", ""})
    public ResponseEntity<List<Movie>> getAllMovies()
    {
        return ResponseEntity.ok(movieService.findAll());
    }
    
    /**
     * this endpoint returns the search criteria and filters by gender of the characters. Implemented in the movieService
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable String id)
    {
        Movie movie = movieService.findMovie(id);
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
            comment.setMovie_id(id);
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
    public ResponseEntity<List<MovieCharacter>> getMovieCharacters(@PathVariable String id,
                                                                   @RequestParam("sortBy") String sortBy,
                                                                   @RequestParam("direction") String direction,
                                                                   @RequestParam("gender") String gender)
    {
        return ResponseEntity.ok(movieService.findMovieCharacters(id, sortBy, direction, gender));
        
    }
    
    
    /**
     * returns a comments for a particular movie sorted in descending order. (ie most recent comments first)
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable String id)
    {
        return ResponseEntity.ok(commentRepository.getMovieComments(id));
    }
}
