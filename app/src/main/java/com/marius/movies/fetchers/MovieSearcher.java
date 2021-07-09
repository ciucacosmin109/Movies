package com.marius.movies.fetchers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieWithGenres;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieSearcher extends AsyncTask<String, Void, List<MovieWithGenres>> {
    // API config
    public static final String apiKey = "36e3586c189d78626382994577137555";
    public static final String apiMovies = "https://api.themoviedb.org/3/search/movie";
    public static final String apiMoviesOptions = "&include_adult=true";

    public static final String apiPosters = "https://image.tmdb.org/t/p/";
    public static final String apiPostersPosterSize = "w92";
    //public static final String apiPostersBackdropSize = "w300";

    // Delegate to send data to the main activity
    public interface onMovieSerchFinishedListener {
        void onMovieSerchFinished(List<MovieWithGenres> result);
    }
    public onMovieSerchFinishedListener delegate;
    public boolean adult;
    public MovieSearcher(onMovieSerchFinishedListener delegate, boolean adult){
        this.delegate = delegate;
        this.adult = adult;
    }
    @Override
    protected void onPostExecute(List<MovieWithGenres> result) {
        if(delegate != null)
            delegate.onMovieSerchFinished(result);
    }

    // Process method
    @Override
    protected List<MovieWithGenres> doInBackground(final String... movieNames) {
        try {
            // Search for the movie
            String movieName = URLEncoder.encode(movieNames[0], "UTF-8");
            URL url = new URL(apiMovies + "?api_key=" + apiKey + (adult ? apiMoviesOptions : "") + "&query=" + movieName);

            // If the genre was not fetched yet:
            if(GenreFetcher.lastGenres == null){
                GenreFetcher.fetchGenres();
            }

            // Parse movies from json
            String result = DataFetcher.fetchData(url);
            return parseMoviesFromJson(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Parse methods
    private List<MovieWithGenres> parseMoviesFromJson(String data) throws JSONException {
        if(data == null || data.length() == 0)
            return null;

        JSONObject json = new JSONObject(data); // exception => throw
        if(!json.has("results"))
            return null;

        JSONArray results = DataParserJson.getJsonArray(json, "results");
        List<MovieWithGenres> movies = new ArrayList<>();

        if(results == null) return null;
        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonMovie = results.getJSONObject(i);
            MovieWithGenres m = new MovieWithGenres();
            m.movie = new Movie();

            try{
                // Get info
                m.movie.setId(DataParserJson.getInt(jsonMovie, "id"));
                m.movie.setTitle(DataParserJson.getString(jsonMovie, "title"));
                m.movie.setOverview(DataParserJson.getString(jsonMovie, "overview"));
                m.movie.setVote_average(DataParserJson.getFloat(jsonMovie, "vote_average"));
                m.movie.setVote_count(DataParserJson.getInt(jsonMovie, "vote_count"));
                m.movie.setPopularity(DataParserJson.getFloat(jsonMovie, "popularity"));
                m.movie.setAdult(DataParserJson.getBoolean(jsonMovie, "adult"));
                m.movie.setOriginal_language(DataParserJson.getString(jsonMovie, "original_language"));
                m.movie.setPoster_path(DataParserJson.getString(jsonMovie, "poster_path"));

                // Get date
                String stringDate = DataParserJson.getString(jsonMovie, "release_date");
                if(stringDate != null && !stringDate.equals("") && !stringDate.equals("null")) {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(stringDate);
                    m.movie.setRelease_date(date);
                }

                // Get genres
                JSONArray jsonGenres = DataParserJson.getJsonArray(jsonMovie, "genre_ids");
                if(jsonGenres != null && jsonGenres.length() > 0) {
                    List<Genre> genres = new ArrayList<>();
                    for (int j = 0; j < jsonGenres.length(); j++) {
                        Genre g = new Genre(jsonGenres.getInt(j));
                        g.setName(GenreFetcher.getGenreNameById(GenreFetcher.lastGenres, g.getId()));
                        genres.add(g);
                    }
                    m.genres = genres;
                }

                // Get image
                String poster = null;
                if(m.movie.getPoster_path() != null && !m.movie.getPoster_path().equals("null") && !m.movie.getPoster_path().equals(""))
                    poster = apiPosters + apiPostersPosterSize + m.movie.getPoster_path();

                if(poster != null)
                    try {
                        m.movie.setPoster_bmp(getBmpFromUrl(new URL(poster)));
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                movies.add(m);
            }catch(JSONException | ParseException e){
                e.printStackTrace();
            }
        }

        return movies;
    }
    public static Bitmap getBmpFromUrl(URL url) throws IOException {
        InputStream stream = url.openConnection().getInputStream();
        return BitmapFactory.decodeStream(stream);
    }

}
