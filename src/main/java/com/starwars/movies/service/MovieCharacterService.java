package com.starwars.movies.service;

import com.starwars.movies.data.DataInitialization;
import com.starwars.movies.entity.Movie;
import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.utility.ConvertToInchesAndFeet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieCharacterService
{
    private DataInitialization dataInitialization = DataInitialization.getInstance();
    
    @Autowired
    MovieService movieService;
    
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
            
            movie.setCharacters(characters);
            
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
                    characters = characters.stream().sorted(Comparator.comparing(MovieCharacter::getName, Comparator.reverseOrder())).collect(Collectors.toCollection(LinkedHashSet::new));
                    // characters.sort(Comparator.comparing(MovieCharacter::getHeight, Comparator.reverseOrder()));
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
}
