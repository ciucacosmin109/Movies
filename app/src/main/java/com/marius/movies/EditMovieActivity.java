package com.marius.movies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.marius.movies.adapters.GenreCheckboxAdapter;
import com.marius.movies.data_access.AppData;
import com.marius.movies.fetchers.GenreFetcher;
import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieWithGenres;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditMovieActivity extends AppCompatActivity implements Button.OnClickListener {
    public static final int REQUEST_CODE_LOAD_IMAGE = 886;

    MovieWithGenres movie;
    int editPosition;

    // Views
    ImageView poster;
    TextInputEditText title;
    TextInputEditText overview;
    RatingBar ratingBar; SeekBar ratingSeekBar;
    SwitchCompat adult;

    boolean posterAdded;

    // Genres spinner
    Spinner genreSpinner;
    GenreCheckboxAdapter genreAdapter;
    List<GenreCheckboxAdapter.CheckableGenre> genres; // trebuie sa aiba si checked .. bool

    // Release date
    Calendar releaseDateCalendar; // To store the value
    TextInputEditText releaseDateTextEdit;

    // Save toolbar
    CheckBox saveFavorites, saveWatchList;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        // Is this an add or an edit ?
        Intent intent = getIntent();
        movie = (MovieWithGenres)intent.getSerializableExtra("oldMovie");
        editPosition = intent.getIntExtra("editPosition", -1);
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            if(movie == null || movie.movie == null){
                ab.setTitle("Add movie");
            } else {
                ab.setTitle("Edit movie");
            }
        }

        // Views
        poster = findViewById(R.id.movie_poster);
        title = findViewById(R.id.movie_title);
        genreSpinner = findViewById(R.id.movie_genres);
        releaseDateTextEdit = (TextInputEditText) findViewById(R.id.movie_date);
        overview = findViewById(R.id.movie_overview);
        ratingBar = findViewById(R.id.movie_rating);
        ratingSeekBar = findViewById(R.id.movie_rating_seek_bar);
        adult = findViewById(R.id.movie_adult_content);
        saveFavorites = findViewById(R.id.save_favorites);
        saveWatchList = findViewById(R.id.save_watchlist);
        saveButton = findViewById(R.id.save_button);

        // Date picker for the release date
        releaseDateCalendar = Calendar.getInstance();
        releaseDateTextEdit.setOnClickListener(releaseDateClicked);

        // Rating bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser)
                    ratingSeekBar.setProgress((int)(rating * 10));
            }
        });
        ratingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    ratingBar.setRating(progress / 10.f);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Poster
        poster.setOnClickListener(addPoster);

        // Save
        saveButton.setOnClickListener(this);
        saveWatchList.setChecked( intent.getBooleanExtra("saveWatchList", false) );
        saveFavorites.setChecked( intent.getBooleanExtra("saveFavorites", false) );
        saveWatchList.setEnabled( !intent.getBooleanExtra("saveDisabled", false) );
        saveFavorites.setEnabled( !intent.getBooleanExtra("saveDisabled", false) );

        // Spinner
        genres = new ArrayList<>();
        genreAdapter = new GenreCheckboxAdapter(this, 0, genres);
        genreSpinner.setAdapter(genreAdapter);
        // Populate spinner
        if(AppData.allGenres == null) {
            AppData.smart_fetchAllGenres(this, new AppData.onGenresFetchedListener() {
                @Override
                public void onFinished(List<Genre> result) {
                    EditMovieActivity.this.populateGenreSpinner(result);
                }
            });
        }else{
            populateGenreSpinner(AppData.allGenres);
        }

        // Set the movie in case of an edit
        this.setMovie(movie);
    }
    private void populateGenreSpinner(List<Genre> genreList){
        if(genreList == null)
            return;

        genres.clear();
        // For each genre, create a checkable genre
        for (int i = 0; i < genreList.size(); i++)
            genres.add(new GenreCheckboxAdapter.CheckableGenre(genreList.get(i)));

        genreAdapter.notifyDataSetChanged();
        this.setGenres(movie);
    }
    // Back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Release Date
    TextInputEditText.OnClickListener releaseDateClicked = new TextInputEditText.OnClickListener() {
        @Override
        public void onClick(View v) {
            new DatePickerDialog(
                    EditMovieActivity.this, releaseDatePickerChanged,
                    releaseDateCalendar.get(Calendar.YEAR),
                    releaseDateCalendar.get(Calendar.MONTH),
                    releaseDateCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        }
    };
    DatePickerDialog.OnDateSetListener releaseDatePickerChanged = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            releaseDateCalendar.set(Calendar.YEAR, year);
            releaseDateCalendar.set(Calendar.MONTH, monthOfYear);
            releaseDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update the release date text input
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            releaseDateTextEdit.setText(sdf.format(releaseDateCalendar.getTime()));
        }
    };

    // Add poster
    View.OnClickListener addPoster = new View.OnClickListener() {
        @Override public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_LOAD_IMAGE);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream stream = getContentResolver().openInputStream(data.getData());

                        Bitmap original = BitmapFactory.decodeStream(stream);
                        Bitmap scaled = Bitmap.createScaledBitmap(original,
                                original.getWidth() / 2, original.getHeight() / 2, true);

                        poster.setImageBitmap(scaled);
                        posterAdded = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    // Save
    @Override
    public void onClick(View v) {
        String error = this.validateFields();
        if(error != null){
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            return;
        }

        MovieWithGenres mv = this.getMovie();
        if(mv != null) {
            Intent intent = getIntent();
            intent.putExtra("movie", mv);
            intent.putExtra("editPosition", editPosition);
            intent.putExtra("saveWatchList", saveWatchList.isChecked());
            intent.putExtra("saveFavorites", saveFavorites.isChecked());
            this.setResult(Activity.RESULT_OK, intent);
            this.finish();
        }else{
            Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
        }
    }
    private String validateFields(){
        if(!saveWatchList.isChecked() && !saveFavorites.isChecked()){
            return "You must select a save location (watchlist/favorites)";
        }
        if (title.getText().toString().trim().length() < 2){
            return "You must enter a valid title";
        }

        return null;
    }
    private MovieWithGenres getMovie() {
        // If there is an ADD, initialize the new movie, else use the old one
        if(movie == null || movie.movie == null) {
            movie = new MovieWithGenres();
            movie.movie = new Movie();
        }

        if(posterAdded){
            movie.movie.setPoster_bmp(((BitmapDrawable) poster.getDrawable()).getBitmap());
        }

        movie.movie.setTitle(title.getText().toString().trim());

        if(releaseDateTextEdit.getText().toString().length() != 0)
            movie.movie.setRelease_date(releaseDateCalendar.getTime());

        if(genres != null) {
            ArrayList<Genre> gr = new ArrayList<>();
            for (int i = 0; i < genres.size(); i++) {
                if (genres.get(i).isChecked)
                    gr.add(genres.get(i).genre);
            }
            movie.genres = (gr.size() != 0 ? gr : null);
        }

        String overviewStr = overview.getText().toString().trim();
        movie.movie.setOverview(!overviewStr.equals("") ? overviewStr : null );

        movie.movie.setMy_vote(ratingBar.getRating());
        movie.movie.setAdult(adult.isChecked());

        return movie;
    }
    private void setMovie(MovieWithGenres m){
        if(m == null || m.movie == null)
            return;

        if(m.movie.getPoster_byte_array() != null && m.movie.getPoster_byte_array().length != 0)
            poster.setImageBitmap(m.movie.getPoster_bmp());

        title.setText(m.movie.getTitle());
        // The genres will be set after the genres fetch
        overview.setText(m.movie.getOverview());
        ratingBar.setRating(m.movie.getMy_vote());
        ratingSeekBar.setProgress((int)(m.movie.getMy_vote() * 10));
        adult.setChecked(m.movie.isAdult());

        // Update the release date text input
        if(m.movie.getRelease_date() != null) {
            releaseDateCalendar.setTime(m.movie.getRelease_date());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            releaseDateTextEdit.setText(sdf.format(releaseDateCalendar.getTime()));
        }
    }
    private void setGenres(MovieWithGenres m){
        if(m == null || m.genres == null || genres == null)
            return;

        List<Genre> mGenres = m.genres;

        for (int i = 0; i < genres.size(); i++){
            for (int j = 0; j < mGenres.size(); j++) {
                if (genres.get(i).genre.getName().compareTo(mGenres.get(j).getName()) == 0){
                    genres.get(i).isChecked = true;
                    break;
                }
            }
        }
        genreAdapter.notifyDataSetChanged();
    }
}