package com.starwars.movies.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.service.MovieCharacterService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class initializes the application with data from the https://swapi.dev API.
 */
@Slf4j
@Getter
@Setter
@Configuration
@Component
public class DataInitialization
{
    public static String MOVIE_API = "https://swapi.dev/api/";
    private List<Movie> movies = new ArrayList<>();
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    MovieCharacterService movieCharacterService;
    
    @Bean
    CommandLineRunner initDatabase()
    {
        return args ->
        {
            log.info("Initializing data.....");
            this.setMovies(getData());
            log.info("Data initialized");
        };
    }
    
    /**
     * Gets the data from https://swapi.dev and transforms it into our Movie entity
     */
    public List<Movie> getData()
    {
        List<Movie> movies = new ArrayList<>();
        
        ResponseEntity<String> response = restTemplate.getForEntity(MOVIE_API + "/films/", String.class);
        
        String output = response.getBody();
        
        Movie movie;
        
        int mCount = 0;
        
        if (output != null)
        {
            JSONObject jsonOutput = new JSONObject(output);
            
            JSONArray moviesArray = jsonOutput.getJSONArray("results"); // retrieves the contents of the results property which is an array
            
            JSONObject jsonMovie;
            while (mCount < moviesArray.length())
            {
                movie = new Movie();
                
                jsonMovie = moviesArray.getJSONObject(mCount); // get each item of the results array
                String title = jsonMovie.getString("title");
                int episodeId = jsonMovie.getInt("episode_id");
                String openingCrawl = jsonMovie.getString("opening_crawl");
                LocalDate releaseDate = LocalDate.parse(jsonMovie.getString("release_date"));
                
                JSONArray characters = jsonMovie.getJSONArray("characters");  // retrieves the characters endpoint url
                
                int count = 0;
                MovieCharacter mc;
                if (characters != null)
                {
                    while (count < characters.length())
                    {
                        String embedUrl = characters.getString(count);
                        
                        response = restTemplate.getForEntity(embedUrl, String.class);
                        String character = response.getBody();
                        mc = movieCharacterService.getMovieCharacterFromJson(character);
                        
                        movie.getCharacters().add(mc); // movie character details added to movie details
                        
                        count++;
                    }
                }
                movie.setMovie_id(String.valueOf(episodeId));
                movie.setTitle(title);
                movie.setEpisode_id(episodeId);
                movie.setOpening_crawl(openingCrawl);
                movie.setRelease_date(releaseDate);
                
                movies.add(movie); // movie with details added to list of movies
                ++mCount;
            }
        }
        movies.sort(Comparator.comparing(Movie::getRelease_date, Comparator.naturalOrder())); // list is sorted by release date
        return movies;
    }
    
    public static void main(String[] args) throws JsonProcessingException
    {
        String ss = "https://swapi.dev/api/films/1/";
        int startFIndex = ss.indexOf("films/", 0);
        String film_id = ss.substring(startFIndex + 6, ss.length() - 1);
        System.out.println("dd: " + film_id);
    }
}
