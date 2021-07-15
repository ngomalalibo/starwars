package com.starwars.movies.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Movie character entity
 * */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "characters")
public class MovieCharacter
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String gender;
    private int height;
    @Transient
    private double heightInches;
    @Transient
    private double heightFeet;
}
