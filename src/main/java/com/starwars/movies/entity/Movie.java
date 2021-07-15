package com.starwars.movies.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
* Movie entity
* */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movies")
public class Movie
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private int episode_id;
    @Lob
    private String opening_crawl;
    private LocalDate release_date;
    @OneToMany(
            cascade = CascadeType.MERGE,
            orphanRemoval = true
    )
    @JoinColumn(name = "movie_id")
    private List<MovieCharacter> characters = new ArrayList<>();
    
    @OneToMany(
            cascade = CascadeType.MERGE,
            orphanRemoval = true
    )
    @JoinColumn(name = "movie_id")
    private List<Comment> comments = new ArrayList<>();
    
    @Transient
    private long commentCount;
}
