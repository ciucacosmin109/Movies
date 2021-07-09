package com.marius.movies.data_access;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.marius.movies.models.Genre;

import java.util.List;

@Dao
public interface GenreDao {
    @Query("SELECT * FROM genres")
    List<Genre> getAll();

    @Query("SELECT * FROM genres WHERE id = :id")
    Genre get(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Genre genre);
    @Query("SELECT id FROM genres WHERE rowid = :rowId")
    int getNewId(Long rowId);

    @Update
    void update(Genre genre);

    @Delete
    void delete(Genre genre);

    @Delete
    void delete(Genre... genre);

    @Query("DELETE FROM genres WHERE id = :id")
    void delete(int id);
}
