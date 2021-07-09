package com.marius.movies.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "genres")
public class Genre implements Serializable {
    // Attributes
    @PrimaryKey(autoGenerate = true) @Exclude //Exclude for firebase
    int id;
    String name;

    // Ctor & getters/setters
    public Genre(){ }
    @Ignore public Genre(int id, String name){
        this.id = id;
        this.name = name;
    }
    @Ignore public Genre(int id){
        this.id = id;
    }
    @Ignore public Genre(String name){
        this.name = name;
    }

    @Exclude public int getId() { return id; }
    @Exclude public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Ignore @Exclude
    public static String getGenresString(List<Genre> genres, int maxGenres){
        // Join all the genres in one string (maxGenres)
        String genresStr = "";
        if(genres != null && genres.size() != 0) {
            for (int i = 0; i < genres.size() && i < maxGenres; i++) {
                genresStr += genres.get(i).getName();

                if (i != genres.size() - 1 && i != maxGenres - 1)
                    genresStr += ", ";
            }
            return genresStr;
        }else return null;
    }
}
