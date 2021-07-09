package com.marius.movies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.marius.movies.EditMovieActivity;
import com.marius.movies.R;
import com.marius.movies.adapters.MovieAdapter;
import com.marius.movies.data_access.AppData;
import com.marius.movies.fetchers.MovieSearcher;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieWithGenres;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment implements ImageButton.OnClickListener, TextView.OnEditorActionListener, MovieSearcher.onMovieSerchFinishedListener {
    public static final int REQUEST_CODE_EDIT_ADD_MOVIE = 210;

    RecyclerView recyclerView;
    TextInputEditText searchText;
    ImageButton searchButton;

    MovieSearcher movieSearcher;
    ProgressBar searchProgress;
    boolean adultContent;

    List<MovieWithGenres> movies;
    MovieAdapter movieAdapter;

    Activity mainActivity;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Custom options
        setHasOptionsMenu(true);
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() != null)
            mainActivity = getActivity();

        // User interface
        searchText = view.findViewById(R.id.search_text);
        searchText.setOnEditorActionListener(this);

        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        searchProgress = view.findViewById(R.id.search_progressBar);
        searchProgress.setVisibility(View.GONE);

        // RecyclerView
        recyclerView = view.findViewById(R.id.search_results);

        movies = new ArrayList<MovieWithGenres>();
        movieAdapter = new MovieAdapter(getContext(), movies, R.menu.movie_item_options_search);
        movieAdapter.setOnItemOptionClickListener(this.onItemOptionClick);

        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Handle screen rotation
        if(savedInstanceState != null){
            String search = savedInstanceState.getString("search");
            if(search != null)
                searchText.setText(search);

            MovieWithGenres[] m = (MovieWithGenres[]) savedInstanceState.getSerializable("results");
            if(m != null) {
                movies.clear();
                movies.addAll(Arrays.asList(m));
                movieAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_search, menu);
    }
    @Override
    public void onMovieSerchFinished(List<MovieWithGenres> result) {
        searchProgress.setVisibility(View.GONE);

        movies.clear();
        if(result != null && result.size() != 0)
            movies.addAll(result);
        else if(result == null)
            Toast.makeText(getContext(), "No network connection", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "No results", Toast.LENGTH_SHORT).show();

        movieAdapter.notifyDataSetChanged();
    }

    // Handle screen rotation = save the list
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search", searchText.getText().toString());
        outState.putSerializable("results", movies.toArray(new MovieWithGenres[0]));
    }

    // When coming from the settings, update the list
    @Override
    public void onResume() {
        super.onResume();
        movieAdapter.notifyDataSetChanged();
    }

    // Click on the search button
    @Override
    public void onClick(View v) {
        // Clear the current search
        movies.clear();
        movieAdapter.notifyDataSetChanged();

        // Get prefs for movie searcher
        if(mainActivity != null) {
            SharedPreferences prefs = mainActivity.getSharedPreferences("preferences", MODE_PRIVATE);
            adultContent = prefs.getBoolean("adult", true);
        } else {
            adultContent = true;
        }

        // Start the search task
        movieSearcher = new MovieSearcher(this, adultContent);
        String title = searchText.getText().toString().trim();
        movieSearcher.execute(title);

        searchProgress.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

            // Start the search
            this.onClick(searchButton);
            return true;
        }
        return false;
    }

    // Click on item options
    MovieAdapter.OnItemOptionClickListener onItemOptionClick = new MovieAdapter.OnItemOptionClickListener() {
        @Override
        public void OnOptionClick(View v, MenuItem item, int position) {
            switch (item.getItemId()) {
                case R.id.movie_item_option_add_wlist:
                    Toast.makeText(getContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();

                    MovieWithGenres mv = movies.get(position);
                    mv.movie.setMovie_list(Movie.MOVIE_LIST.WATCHLIST);
                    AppData.watchlistMovies.add(mv);
                    if(mainActivity != null)
                        AppData.room_insertMovieWithGenres(mainActivity, mv);
                    break;
                case R.id.movie_item_option_add_fav:
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();

                    mv = movies.get(position);
                    mv.movie.setMovie_list(Movie.MOVIE_LIST.FAVORITES);
                    AppData.favoritesMovies.add(mv);
                    if(mainActivity != null)
                        AppData.room_insertMovieWithGenres(mainActivity, mv);
                    break;
                case R.id.movie_item_option_edit:
                    Intent intent = new Intent(getContext(), EditMovieActivity.class);
                    intent.putExtra("oldMovie", movies.get(position));
                    intent.putExtra("editPosition", position);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_ADD_MOVIE);

                    break;
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data == null)
            return;

        switch (requestCode){
            case REQUEST_CODE_EDIT_ADD_MOVIE:
                MovieWithGenres mv = (MovieWithGenres)data.getSerializableExtra("movie");
                if(data.getBooleanExtra("saveWatchList", false)){
                    mv.movie.setMovie_list(Movie.MOVIE_LIST.WATCHLIST);
                    AppData.watchlistMovies.add(mv);
                    if(mainActivity != null)
                        AppData.room_insertMovieWithGenres(mainActivity, mv);
                }
                if(data.getBooleanExtra("saveFavorites", false)){
                    MovieWithGenres mv1 = mv;
                    mv = new MovieWithGenres();
                    mv.movie = (Movie)mv1.movie.clone();
                    mv.movie.setId(0);
                    mv.genres = mv1.genres;

                    mv.movie.setMovie_list(Movie.MOVIE_LIST.FAVORITES);
                    AppData.favoritesMovies.add(mv);
                    if(mainActivity != null)
                        AppData.room_insertMovieWithGenres(mainActivity, mv);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
