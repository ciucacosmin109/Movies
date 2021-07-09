package com.marius.movies.fetchers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenreFetcher  extends AsyncTask<Void, Void, List<Genre>> {
    // API config
    public static final String apiKey = "36e3586c189d78626382994577137555";
    public static final String apiGenres = "https://api.themoviedb.org/3/genre/movie/list";

    // Result of the last request
    public static List<Genre> lastGenres;

    // Delegate to send data to the main activity
    public interface onGenreFetchFinishedListener {
        void onGenreFetchFinished(List<Genre> result);
    }
    public GenreFetcher.onGenreFetchFinishedListener delegate;
    public GenreFetcher(GenreFetcher.onGenreFetchFinishedListener delegate) { this.delegate = delegate; }
    @Override
    protected void onPostExecute(List<Genre> result) {
        if(delegate != null)
            delegate.onGenreFetchFinished(result);
    }

    // Process method
    @Override
    protected List<Genre> doInBackground(Void... voids) {
        try {
            fetchGenres();
            return lastGenres;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Genre> fetchGenres() throws JSONException, IOException {
        String genresJson = DataFetcher.fetchData(new URL(apiGenres + "?api_key=" + apiKey));
        lastGenres = parseGenresFromJson(genresJson);
        return lastGenres;
    }

    // Parse method
    public static List<Genre> parseGenresFromJson(String data) throws JSONException {
        if (data == null || data.length() == 0)
            return null;

        JSONObject json = new JSONObject(data); // exception => throw
        if (!json.has("genres"))
            return null;

        JSONArray results = json.getJSONArray("genres");
        ArrayList<Genre> genresList = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonGenre = results.getJSONObject(i);
            Genre g = new Genre();

            try {
                // Get info
                g.setId(DataParserJson.getInt(jsonGenre, "id"));
                g.setName(DataParserJson.getString(jsonGenre, "name"));

                genresList.add(g);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return genresList;

    }
    public static String getGenreNameById(List<Genre> genreList, int id){
        if(genreList == null)
            return null;

        for (int i = 0; i < genreList.size(); i++) {
            if(genreList.get(i).getId() == id)
                return genreList.get(i).getName();
        }
        return null;
    }

}