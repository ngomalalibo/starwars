package com.starwars.movies.service;

import com.starwars.movies.data.DataInitialization;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.utility.ConvertToInchesAndFeet;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieCharacterService
{
    @Autowired
    private DataInitialization dataInitialization;
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Set<MovieCharacter> getMovieChars(String id, String sortBy, String direction, String gender)
    {
        Movie movie = dataInitialization.getMovies().stream().filter(m -> m.getMovie_id().equals(id)).findAny().orElse(null);
        
        Set<MovieCharacter> characters = new HashSet<>();
        
        if (movie != null)
        {
            if (gender != null)
            {
                characters = movie.getCharacters().stream().filter(c -> c.getGender().equalsIgnoreCase(gender)).collect(Collectors.toSet());
            }
            else
            {
                characters = movie.getCharacters();
            }
            
            if (sortBy.equals("name"))
            {
                if (direction.equalsIgnoreCase("asc"))
                {
                    characters = characters.stream().sorted(Comparator.comparing(MovieCharacter::getName, Comparator.naturalOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                }
                else
                {
                    characters = characters.stream().sorted(Comparator.comparing(MovieCharacter::getName, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                }
            }
            else if (sortBy.equals("height"))
            
            {
                if (direction.equalsIgnoreCase("asc"))
                {
                    characters = characters.stream().sorted(Comparator.comparing(MovieCharacter::getHeight, Comparator.naturalOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                }
                else
                {
                    characters = characters.stream().sorted(Comparator.comparing(MovieCharacter::getHeight, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                }
            }
            characters.stream().peek(this::getMovieCharacterDetails); // sort before getting details. more performant
        }
        return characters;
    }
    
    public MovieCharacter getMovieCharacterDetails(MovieCharacter character)
    {
        character.setHeightInches(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderInches(character.getHeight()));
        character.setHeightFeet(ConvertToInchesAndFeet.getMovieCharTotalHeightForGenderFeet(character.getHeight()));
        return character;
    }
    
    public long getMovieCharTotalHeightForGenderCM(String id, String sortBy, String direction, String gender)
    {
        Set<MovieCharacter> movieCharacters = movieService.findMovieCharacters(id, sortBy, direction, gender);
        return movieCharacters.stream().map(MovieCharacter::getHeight).reduce(Integer::sum).orElse(0);
    }
    
    public long getMovieCharTotalForGender(String id, String sortBy, String direction, String gender)
    {
        return movieService.findMovieCharacters(id, sortBy, direction, gender).size();
    }
    
    public MovieCharacter findMovieCharacter(String cid)
    {
        ResponseEntity<String> response = restTemplate.getForEntity(DataInitialization.MOVIE_API + "/people/" + cid, String.class);
        String character = response.getBody();
        
        return getMovieCharacterFromJson(character);
        
    }
    
    public MovieCharacter getMovieCharacterFromJson(String character)
    {
        MovieCharacter mc = new MovieCharacter();
        
        JSONObject charJson = new JSONObject(character);
        String url = charJson.getString("url");
        int startIndex = url.indexOf("people/", 0);
        String characterID = url.substring(startIndex + 7, url.length() - 1);
        
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
                
                fCount++;
            }
        }
        
        return getMovieCharacterDetails(mc);
    }
    
}
