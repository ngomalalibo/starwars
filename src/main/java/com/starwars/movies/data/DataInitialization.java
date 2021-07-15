package com.starwars.movies.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.repository.MovieCharRepository;
import com.starwars.movies.repository.MovieRepository;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * This class initializes the application database with data from the https://swapi.dev API.
 */
@Slf4j
@Configuration
public class DataInitialization
{
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    MovieCharRepository movieCharRepository;
    
    
    private static String MOVIE_API = "https://swapi.dev/api/";
    
    HttpClient httpClient = HttpClient.create()
                                      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                      .responseTimeout(Duration.ofMillis(5000))
                                      .doOnConnected(conn ->
                                                             conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                                                 .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
    
    WebClient client = WebClient.builder()
                                .baseUrl(MOVIE_API)
                                .defaultCookie("cookieKey", "cookieValue")
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .defaultUriVariables(Collections.singletonMap("url", MOVIE_API))
                                .clientConnector(new ReactorClientHttpConnector(httpClient))
                                .build();
    
    // WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.post();
    
    @Bean
    @Transactional
    CommandLineRunner initDatabase()
    {
        return args ->
        {
            log.info("Initializing database.....");
            getData();
            log.info("Database initialized");
        };
    }
    
    /**
     * Gets the data from https://swapi.dev and transforms it into our Movie entity
     */
    public void getData()
    {
        String output = client.get().uri(MOVIE_API + "/films/").retrieve().bodyToMono(String.class).block();
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        
        
        Movie movie;
        MovieCharacter mc;
        int mCount = 0;
        
        if (output != null)
        {
            JSONObject jsonOutput = new JSONObject(output);
            /*JsonElement je = jp.parse(output);
            String prettyJsonResponse = gson.toJson(je);
            log.info("output -> " + prettyJsonResponse);*/ // prints initial swapi response
            
            JSONArray movies = jsonOutput.getJSONArray("results"); // retrieves the contents of the results property which is an array
            
            JSONObject jsonMovie;
            while (mCount < movies.length())
            {
                movie = new Movie();
                
                JsonElement je = jp.parse(movies.toString());
                String prettyJsonResponse = gson.toJson(je);
                //log.info("result>>>>>>>>>>>>: " + prettyJsonResponse);
                
                jsonMovie = movies.getJSONObject(mCount); // get each item of the results array
                
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
                        String character = client.get().uri(embedUrl).retrieve().bodyToMono(String.class).block();
                        
                        JSONObject charJson = new JSONObject(character);
                        
                        long characterID = Long.parseLong(embedUrl.substring(embedUrl.length() - 2, embedUrl.length() - 1));
                        String name = charJson.getString("name");
                        int height = 0;
                        String height1 = charJson.getString("height");
                        if (StringUtils.isNumeric(height1))
                        {
                            height = Integer.parseInt(height1);
                        }
                        String gender = charJson.getString("gender");
                        
                        mc.setId(characterID);
                        mc.setName(name);
                        mc.setHeight(height);
                        mc.setGender(gender);
                        
                        movieCharRepository.save(mc);
                        movie.getCharacters().add(mc);
                        
                        count++;
                    }
                }
                movie.setTitle(title);
                movie.setEpisode_id(episodeId);
                movie.setOpening_crawl(openingCrawl);
                movie.setRelease_date(releaseDate);
                
                movieRepository.save(movie);
                ++mCount;
            }
        }
    }
}
