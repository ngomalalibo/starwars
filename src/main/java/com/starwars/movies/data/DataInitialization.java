package com.starwars.movies.data;

import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * This class initializes the application database with data from the https://swapi.dev API.
 */
@Slf4j
@Component
@Configuration
public class DataInitialization
{
    private static String MOVIE_API = "https://swapi.dev/api/";
    
    @Autowired
    RestTemplate restTemplate;
    
    @Bean
    CommandLineRunner initDatabase()
    {
        return args ->
        {
            log.info("Initializing data.....");
            getMovies();
            log.info("Data initialized");
        };
    }
    
    /**
     * Gets the data from https://swapi.dev and transforms it into our Movie entity
     */
    public List<Movie> getMovies()
    {
        List<Movie> movies = new ArrayList<>();
        
        ResponseEntity<String> response = restTemplate.getForEntity(MOVIE_API + "/films/", String.class);
        
        String output = response.getBody();
        
        Movie movie;
        MovieCharacter mc;
        
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
                if (characters != null)
                {
                    mc = new MovieCharacter();
                    while (count < characters.length())
                    {
                        String embedUrl = characters.getString(count);
                        response = restTemplate.getForEntity(embedUrl, String.class);
                        String character = response.getBody();
                        
                        JSONObject charJson = new JSONObject(character);
                        int startIndex = embedUrl.indexOf("people/", 0);
                        String characterID = embedUrl.substring(startIndex + 7, embedUrl.length() - 1);
                        
                        String url = charJson.getString("url");
                        String name = charJson.getString("name");
                        int height = 0;
                        String height1 = charJson.getString("height");
                        if (StringUtils.isNumeric(height1))
                        {
                            height = Integer.parseInt(height1);
                        }
                        String gender = charJson.getString("gender");
                        
                        mc.setMovie_character_id(characterID);
                        mc.setName(name);
                        mc.setHeight(height);
                        mc.setGender(gender);
                        mc.setUrl(url);
                        
                        JSONArray films = charJson.getJSONArray("films");
                        int fCount = 0;
                        if (films != null)
                        {
                            while (fCount < films.length())
                            {
                                String fUrl = films.getString(fCount);
                                int startFIndex = fUrl.indexOf("films/", 0);
                                String film_id = fUrl.substring(startFIndex + 6, fUrl.length() - 1);
                                mc.getMovie_ids().add(film_id); // add file id to character details
                                
                                // response = restTemplate.getForEntity(fUrl, String.class); // already have film details
                                // String film = response.getBody();
                                // JSONObject fJson = new JSONObject(film);
                                
                                fCount++;
                            }
                        }
                        
                        
                        movie.getCharacters().add(mc); // movie character details added to movie details
                        
                        count++;
                    }
                }
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
    
    /*public static void main(String[] args) throws JsonProcessingException
    {
        DataInitialization d = new DataInitialization();
        List<Movie> movies = d.getMovies();
        
        Movie movie = movies.get(0);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(movie.getCharacters());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(s);
        String prettyJsonResponse = gson.toJson(je);
        System.out.println("Movie: " + prettyJsonResponse);
    }*/
}
