package com.starwars.movies.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class MovieCharPage
{
    private int pageNumber = 0;
    private int pageSize = 10;
    private Sort.Direction direction = Sort.Direction.ASC;
    private String sortBy = "gender";
    
}
