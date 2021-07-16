package com.starwars.movies.service;

import com.starwars.movies.data.DataInitialization;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.utility.ConvertToInchesAndFeet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieCharacterService
{
    @Autowired
    DataInitialization dataInitialization;
    
    @Autowired
    MovieService movieService;
    
    public List<MovieCharacter> getMovieChars(String id, String sortBy, String direction, String gender)
    {
        Movie movie = dataInitialization.getMovies().stream().filter(m -> m.getMovie_id().equals(id)).findAny().orElse(null);
    
        List<MovieCharacter> characters = new ArrayList<>();
    
        if (movie != null)
        {
            if (gender != null)
            {
                characters = movie.getCharacters().stream().filter(c -> c.getGender().equalsIgnoreCase(gender)).collect(Collectors.toList());
            }
        
            movie.setCharacters(characters);
        
            if (sortBy.equals("name"))
            {
                if (direction.equalsIgnoreCase("asc"))
                {
                    characters.sort(Comparator.comparing(MovieCharacter::getName, Comparator.naturalOrder()));
                }
                else
                {
                    characters.sort(Comparator.comparing(MovieCharacter::getName, Comparator.reverseOrder()));
                }
            }
            else if (sortBy.equals("height"))
        
            {
                if (direction.equalsIgnoreCase("asc"))
                {
                    characters.sort(Comparator.comparing(MovieCharacter::getHeight, Comparator.naturalOrder()));
                }
                else
                {
                    characters.sort(Comparator.comparing(MovieCharacter::getHeight, Comparator.reverseOrder()));
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
        List<MovieCharacter> movieCharacters = movieService.findMovieCharacters(id, sortBy, direction, gender);
        return movieCharacters.stream().map(MovieCharacter::getHeight).reduce(Integer::sum).orElse(0);
    }
    
    public long getMovieCharTotalForGender(String id, String sortBy, String direction, String gender)
    {
        return movieService.findMovieCharacters(id, sortBy, direction, gender).size();
    }
}
