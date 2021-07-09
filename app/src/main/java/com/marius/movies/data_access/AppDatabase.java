package com.marius.movies.data_access;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieGenreCrossRef;

@Database(entities = { Movie.class, Genre.class, MovieGenreCrossRef.class }, version = 1, exportSchema = false)
@TypeConverters({Movie.DateConverter.class, Movie.MovieListEnumConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "movie_database";
    private static final Object LOCK = new Object();
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static AppDatabase database;

    public abstract MovieDao getMovieDao();
    public abstract GenreDao getGenreDao();
    public abstract MovieGenreCrossRefDao getCrossRefDao();

    public static AppDatabase getInstance(Context context) {
        if(database!=null)
            return database;

        synchronized (LOCK){
            if (database == null) {
                Log.d(LOG_TAG, "Db instance created");
                database = Room.databaseBuilder(
                    context,
                    AppDatabase.class,
                    DB_NAME
                ).build();
            }
        }
        return database;
    }

}
