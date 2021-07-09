package com.marius.movies.data_access;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieGenreCrossRef;
import com.marius.movies.models.MovieWithGenres;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies WHERE movie_list = :movie_list")
    List<Movie> getAll(Movie.MOVIE_LIST movie_list);

    @Query("SELECT * FROM movies WHERE id = :id")
    Movie get(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Movie movie);
    @Query("SELECT id FROM movies WHERE rowid = :rowId")
    int getNewId(Long rowId);

    @Update
    void update(Movie movie);

    @Delete
    void delete(Movie movie);

    @Delete
    void delete(Movie... movie);

    @Query("DELETE FROM movies WHERE id = :id")
    void delete(int id);


}
