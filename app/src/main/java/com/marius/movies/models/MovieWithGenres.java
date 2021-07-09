package com.marius.movies.models;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;
import androidx.room.Junction;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovieWithGenres implements Serializable {
    @Embedded public Movie movie;
    @Relation(
        parentColumn = "id",
        entity = Genre.class,
        entityColumn = "id",
        associateBy = @Junction(
            value = MovieGenreCrossRef.class,
            parentColumn = "movie_id",
            entityColumn = "genre_id"
        )
    )
    public List<Genre> genres; // la search primesc doar id ...

}
