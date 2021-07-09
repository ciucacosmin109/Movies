package com.marius.movies.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "moviegenrecrossref",
        primaryKeys = {"movie_id", "genre_id"},
        foreignKeys = {
            @ForeignKey(entity = Movie.class,
                parentColumns = "id",
                childColumns = "movie_id",
                onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Genre.class,
                parentColumns = "id",
                childColumns = "genre_id",
                onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "movie_id"), @Index(value = "genre_id")}
)
public class MovieGenreCrossRef {
    public int movie_id;
    public int genre_id;
}
