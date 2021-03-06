package com.starwars.movies.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Movie entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie
{
    private String movie_id;
    private String title;
    private int episode_id;
    private String opening_crawl;
    private LocalDate release_date;
    private Set<MovieCharacter> characters = new HashSet<>();
    private List<Comment> comments = new ArrayList<>();
    private long commentCount;
}
