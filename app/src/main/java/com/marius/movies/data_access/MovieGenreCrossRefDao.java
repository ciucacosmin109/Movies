package com.marius.movies.data_access;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieGenreCrossRef;
import com.marius.movies.models.MovieWithGenres;

import java.util.List;

@Dao
public interface MovieGenreCrossRefDao {

    @Transaction @Query("SELECT * FROM movies WHERE movie_list = :movie_list")
    List<MovieWithGenres> getAllMoviesWithGenres(Movie.MOVIE_LIST movie_list);

//    void insert(MovieWithGenres mwg){
//        int id = insert(mwg.movie);
//        for(int i =0;i<mwg.genres.size();i++){
//            int gId = insertGenre(mwg.genres.get(i));
//
//            MovieGenreCrossRef cr = new MovieGenreCrossRef();
//            cr.movie_id = id;
//            cr.genre_id = gId;
//            insertMovieGenreCrossRef(cr);
//        }
//    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MovieGenreCrossRef mgcr);
    @Delete
    void delete(MovieGenreCrossRef mgcr);
    @Query("DELETE FROM moviegenrecrossref WHERE movie_id = :movie_id")
    void delete(int movie_id);

    @Transaction @Delete
    void deleteMoviesWithGenres(Movie movie, List<Genre> genres);
}
