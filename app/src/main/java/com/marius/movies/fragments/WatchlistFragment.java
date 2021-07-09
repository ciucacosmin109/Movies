package com.marius.movies.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marius.movies.EditMovieActivity;
import com.marius.movies.R;
import com.marius.movies.adapters.MovieAdapter;
import com.marius.movies.data_access.AppData;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieWithGenres;

import java.util.List;

public class WatchlistFragment extends Fragment {
    public static final int REQUEST_CODE_ADD_MOVIE = 200;
    public static final int REQUEST_CODE_EDIT_MOVIE = 201;

    RecyclerView recyclerView;
    ProgressBar progressBar;

    MovieAdapter movieAdapter;
    List<MovieWithGenres> movies;

    Activity mainActivity;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Custom options
        setHasOptionsMenu(true);
        setRetainInstance(true);

        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_fav_wlist, menu);
    }

    // Add/Edit movie click handlers
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_movie:
                Intent intent = new Intent(getContext(), EditMovieActivity.class);
                intent.putExtra("saveWatchList", true);
                intent.putExtra("saveDisabled", true);
                startActivityForResult(intent, REQUEST_CODE_ADD_MOVIE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    MovieAdapter.OnItemOptionClickListener onItemOptionClick = new MovieAdapter.OnItemOptionClickListener() {
        @Override
        public void OnOptionClick(View v, MenuItem item, int position) {
            switch(item.getItemId()){
                case R.id.movie_item_option_edit:
                    Intent intent = new Intent(getContext(), EditMovieActivity.class);
                    intent.putExtra("saveWatchList", true);
                    intent.putExtra("saveDisabled", true);
                    intent.putExtra("oldMovie", movies.get(position));
                    intent.putExtra("editPosition", position);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_MOVIE);

                    break;
                case R.id.movie_item_option_delete:
                    // Remove from wl table
                    if(mainActivity != null) {
                        AppData.room_deleteMovieWithGenres(mainActivity, movies.get(position));
                    }

                    // Remove from UI
                    movies.remove(position);
                    movieAdapter.notifyDataSetChanged();

                    break;
                case R.id.movie_item_option_move:
                    // Remove from wl table & add to fav table
                    if(mainActivity != null) {
                        movies.get(position).movie.setMovie_list(Movie.MOVIE_LIST.FAVORITES);
                        AppData.room_updateMovieWithGenres(mainActivity, movies.get(position));
                    }

                    // Add to fav
                    AppData.favoritesMovies.add(movies.get(position));

                    // Remove from UI
                    movies.remove(position);
                    movieAdapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
        }
    };

    // Handle the return from EditMovieActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data == null)
            return;

        switch (requestCode){
            case REQUEST_CODE_ADD_MOVIE:
                MovieWithGenres newMv = (MovieWithGenres)data.getSerializableExtra("movie");
                movies.add(newMv);
                movieAdapter.notifyDataSetChanged();

                if(mainActivity != null) {
                    newMv.movie.setMovie_list(Movie.MOVIE_LIST.WATCHLIST);
                    AppData.room_insertMovieWithGenres(mainActivity, newMv); 
                }

                break;
            case REQUEST_CODE_EDIT_MOVIE:
                MovieWithGenres mv = (MovieWithGenres)data.getSerializableExtra("movie");
                int pos = data.getIntExtra("editPosition", -1);
                movies.set(pos, mv);
                movieAdapter.notifyDataSetChanged();

                if(mainActivity != null) {
                    mv.movie.setMovie_list(Movie.MOVIE_LIST.WATCHLIST);
                    AppData.room_updateMovieWithGenres(mainActivity, mv);
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    // When coming from the settings, update the list
    @Override
    public void onResume() {
        super.onResume();
        movieAdapter.notifyDataSetChanged();
    }

    // Init the controls
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movies = AppData.watchlistMovies;

        recyclerView = view.findViewById(R.id.watchlist_movies);
        movieAdapter = new MovieAdapter(getContext(), movies, R.menu.movie_item_options_wlist);
        movieAdapter.setOnItemOptionClickListener(onItemOptionClick);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progressBar);

        if(getActivity() != null) {
            mainActivity = getActivity();
            AppData.room_fetchMovieWithGenres(mainActivity, Movie.MOVIE_LIST.WATCHLIST, new AppData.onMoviesFetchedListener() {
                @Override
                public void onFinished(List<MovieWithGenres> result) {
                    movies.clear();
                    movies.addAll(result);
                    movieAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}
