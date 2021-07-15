package com.starwars.movies.service;

import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.model.MovieCharPage;
import com.starwars.movies.model.MovieCharSearchCriteria;
import com.starwars.movies.repository.MovieCharCriteriaRepository;
import com.starwars.movies.repository.MovieCharRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieCharacterService
{
    
    
    @Autowired
    MovieCharRepository movieCharRepository;
    
    @Autowired
    MovieCharCriteriaRepository movieCharCriteriaRepository;
    
    public List<MovieCharacter> getMovieChars(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, long id)
    {
        return movieCharCriteriaRepository.findAllWithFilters(movieCharPage, movieCharSearchCriteria, id).getContent();
    }
    
    public long getMovieCharTotalHeightForGenderCM(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, long id)
    {
        List<MovieCharacter> content = movieCharCriteriaRepository.findAllWithFilters(movieCharPage, movieCharSearchCriteria, id).getContent();
        return content.stream().map(MovieCharacter::getHeight).reduce(Integer::sum).orElse(0);
    }
    
    public long getMovieCharTotalForGender(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, long id)
    {
        return movieCharCriteriaRepository.findAllWithFilters(movieCharPage, movieCharSearchCriteria, id).getTotalElements();
    }
}
