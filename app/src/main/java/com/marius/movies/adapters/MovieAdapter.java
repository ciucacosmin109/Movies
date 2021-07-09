package com.marius.movies.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.marius.movies.R;
import com.marius.movies.models.Genre;
import com.marius.movies.models.MovieWithGenres;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<MovieWithGenres> movies;
    int movieItemOptions;

    public MovieAdapter(Context context, List<MovieWithGenres> movies, int movieItemOptions ){
        this.context = context;
        this.movies = movies;
        this.movieItemOptions = movieItemOptions;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        MovieWithGenres m = movies.get(position);

        // Set the title
        String title = m.movie.getTitle();
        if(title != null) holder.title.setText(title);
        else holder.title.setText("???");

        // Genres
        SharedPreferences prefs = context.getSharedPreferences("preferences", MODE_PRIVATE);
        String mgStr = prefs.getString("nr_of_genres", "3");
        int maxGenres = mgStr != null ? Integer.parseInt(mgStr) : 3;

        String genres = Genre.getGenresString(m.genres,maxGenres);
        if(genres != null) holder.genres.setText(genres);
        else holder.genres.setText("-");

        String yearStr = m.movie.getRelease_date_year_string();
        if(yearStr != null) holder.year.setText(yearStr);
        else holder.year.setText("-");

        // Set the ratings
        float tmdb = m.movie.getVote_average();
        float my = m.movie.getMy_vote();
        Resources res = context.getResources();
        holder.rating.setText(String.format(res.getString(R.string.movie_rating), tmdb));
        holder.myRating.setText(String.format(res.getString(R.string.movie_p_rating), my));

        // Set the poster
        Bitmap bmp = m.movie.getPoster_bmp();
        if(bmp != null) holder.image.setImageBitmap(bmp);
        else holder.image.setImageResource(R.drawable.ic_baseline_local_movies_24);

        // Setup the options
        holder.options.setOnClickListener(optionsClick(position));

    }
    protected View.OnClickListener optionsClick(final int position){
        return new View.OnClickListener() {
            @Override public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.inflate(MovieAdapter.this.movieItemOptions);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(final MenuItem item) {
                        if(delegate != null){
                            delegate.OnOptionClick(v, item, position);
                            return true;
                        } else return false;
                    }
                });
                popup.show();
            }
        };
    }

    @Override
    public int getItemCount() { return movies.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, genres, year;
        TextView rating, myRating;
        Button options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.movie_item_image);
            title = itemView.findViewById(R.id.movie_item_title);
            genres = itemView.findViewById(R.id.movie_item_genres);
            year = itemView.findViewById(R.id.movie_item_year);
            rating = itemView.findViewById(R.id.movie_item_rating);
            myRating = itemView.findViewById(R.id.movie_item_personal_rating);
            options = itemView.findViewById(R.id.movie_item_options);
        }
    }

    // Delegate for the click on the item's options
    public interface OnItemOptionClickListener {
        void OnOptionClick(View v, MenuItem item, int position);
    }
    public OnItemOptionClickListener delegate;
    public void setOnItemOptionClickListener(OnItemOptionClickListener delegate){
        this.delegate = delegate;
    }


}