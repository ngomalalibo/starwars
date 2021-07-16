package com.starwars.movies.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Movie character entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieCharacter
{
    private String movie_character_id;
    private String name;
    private String gender;
    private int height;
    private double heightInches;
    private double heightFeet;
    private List<String> movie_ids = new ArrayList<>();
    private String url;
}
