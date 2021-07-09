package com.marius.movies.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.firebase.database.Exclude;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "movies")
@TypeConverters({Movie.DateConverter.class, Movie.MovieListEnumConverter.class})
public class Movie implements Serializable, Cloneable{
    // Basic
    @PrimaryKey(autoGenerate = true) @Exclude //Exclude for firebase
    int id;
    String title;
    Date release_date;
    String overview;

    String poster_path;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) @Exclude //Exclude for firebase
    public byte[] poster_byte_array;
    boolean adult;

    float my_vote;
    float vote_average;
    int vote_count;
    float popularity;

    String original_language; // iso_639_1

    // Movie details
    String imdb_id; // primary key pt firebase ?
    int runtime;

    // Extra table fields
    public enum MOVIE_LIST {WATCHLIST, FAVORITES};
    MOVIE_LIST movie_list;

    // Constructors
    public Movie(){ }
    @Ignore public Movie(String title){ this.title = title; }

    // Getters and Setters
    @Exclude public int getId() { return id; }
    @Exclude public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getRelease_date() { return release_date; }
    public void setRelease_date(Date release_date) { this.release_date = release_date; }
    @Ignore @Exclude public String getRelease_date_year_string(){
        Date date = this.getRelease_date();
        if(date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int y = calendar.get(Calendar.YEAR);
            return String.valueOf(y);
        }else{
            return null;
        }
    }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPoster_path() { return poster_path; }
    public void setPoster_path(String poster_path) { this.poster_path = poster_path; }

    @Exclude public byte[] getPoster_byte_array() { return poster_byte_array; }
    @Exclude public void setPoster_byte_array(byte[] poster_byte_array) { this.poster_byte_array = poster_byte_array; }
    @Ignore @Exclude public Bitmap getPoster_bmp() {
        //return poster_bmp;
        if(poster_byte_array == null || poster_byte_array.length == 0)
            return null;

        return BitmapFactory.decodeByteArray(poster_byte_array, 0, poster_byte_array.length);
    }
    @Ignore @Exclude public void setPoster_bmp(Bitmap bmp) {
        //this.poster_bmp = bmp;
        if(bmp == null)
            return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

        poster_byte_array = stream.toByteArray();
    }

    public boolean isAdult() { return adult; }
    public void setAdult(boolean adult) { this.adult = adult; }

    public float getVote_average() { return vote_average; }
    public void setVote_average(float vote_average) { this.vote_average = vote_average; }

    public int getVote_count() { return vote_count; }
    public void setVote_count(int vote_count) { this.vote_count = vote_count; }

    public float getPopularity() { return popularity; }
    public void setPopularity(float popularity) { this.popularity = popularity; }

    public float getMy_vote() { return my_vote; }
    public void setMy_vote(float vote) { this.my_vote = vote; }

    public String getOriginal_language() { return original_language; }
    public void setOriginal_language(String original_language) { this.original_language = original_language; }

    public String getImdb_id() { return imdb_id; }
    public void setImdb_id(String imdb_id) { this.imdb_id = imdb_id; }

    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }

    public MOVIE_LIST getMovie_list() {
        return movie_list;
    }
    public void setMovie_list(MOVIE_LIST movie_list) {
        this.movie_list = movie_list;
    }

    // Clone
    @NonNull @Override
    public Object clone() {
        Movie m;
        try {
            m = (Movie) super.clone();
        } catch (CloneNotSupportedException e) {
            m = new Movie();
            m.setId(this.id);
            m.setTitle(this.title);
            m.setRelease_date(this.release_date);
            m.setOverview(this.overview);

            m.setPoster_path(this.poster_path);
            m.setPoster_byte_array(this.poster_byte_array);
            m.setAdult(this.adult);

            m.setMy_vote(this.my_vote);
            m.setVote_average(this.vote_average);
            m.setVote_count(this.vote_count);
            m.setPopularity(this.popularity);
            m.setOriginal_language(this.original_language);

            m.setImdb_id(this.imdb_id);
            m.setRuntime(this.runtime);
            m.setMovie_list(this.movie_list);
        }
        return m;
    }

    // Converters for Room
    public static class DateConverter {
        @TypeConverter
        public static Date toDate(Long dateLong){
            return dateLong == null ? null: new Date(dateLong);
        }

        @TypeConverter
        public static Long fromDate(Date date){
            return date == null ? null : date.getTime();
        }
    }
    public static class MovieListEnumConverter {
        @TypeConverter
        public static MOVIE_LIST toMovieList(int nr){
            return MOVIE_LIST.values()[(nr >= 0 && nr < MOVIE_LIST.values().length) ? nr : 0];
        }
        @TypeConverter
        public static int fromMovieList(MOVIE_LIST ml){
            return ml != null ? ml.ordinal() : 0;
        }
    }

}
