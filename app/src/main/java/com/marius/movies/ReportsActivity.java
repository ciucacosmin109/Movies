package com.marius.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.marius.movies.data_access.AppData;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieWithGenres;
import com.marius.movies.models.ReportPair;
import com.marius.movies.models.TableReport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {
    public static final float STEP = 1f;

    protected String pair_id;
    protected List<Float> ratings;
    protected TableReport tableReport;

    protected boolean allowSave = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Reports");
        }

        ReportPair pair = (ReportPair)getIntent().getSerializableExtra("report_pair");
        if(pair != null){
            allowSave = false;
            pair_id = pair.getFirebase_id();

            initPie(pair.getPie());
            initTable(pair.getTable());
        } else AppData.room_fetchMovieWithGenres(this, Movie.MOVIE_LIST.FAVORITES, new AppData.onMoviesFetchedListener() {
            @Override
            public void onFinished(List<MovieWithGenres> result) {
                final List<MovieWithGenres> favorites = result;

                // Init Pie
                if(result != null && result.size() > 0) {
                    ratings = new ArrayList<Float>();
                    for(int i = 0; i < result.size(); i++){
                        ratings.add(result.get(i).movie.getVote_average());
                    }
                    initPie(ratings);
                }

                // Init table
                AppData.room_fetchMovieWithGenres(ReportsActivity.this, Movie.MOVIE_LIST.WATCHLIST, new AppData.onMoviesFetchedListener() {
                    @Override
                    public void onFinished(List<MovieWithGenres> result) {
                        List<MovieWithGenres> watchlist = result;
                        initTable(watchlist, favorites);
                    }
                });
            }
        });
    }
    // Back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Save to firebase
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(allowSave ? R.menu.toolbar_menu_report_save : R.menu.toolbar_menu_report_delete, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Name for the report pair");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String friendlyName = input.getText().toString();
                    AppData.firebase_insertReport(new ReportPair(ratings, tableReport, friendlyName));
                    Toast.makeText(ReportsActivity.this, "Saved to firebase", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();
            return true;
        }else if(item.getItemId() == R.id.delete){
            AppData.firebase_deleteReport(pair_id);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Pie chart
    protected void initPie(List<Float> ratings){
        if(ratings != null && ratings.size() > 0){
            PieChart pieChart = findViewById(R.id.pieChart);
            pieChart.setDrawSliceText(false);
            List<Entry> values = new ArrayList<>();
            List<String> desc = new ArrayList<>();

            // Process data
            int[] vals = new int[10];
            for(int i=0;i<ratings.size();i++){
                int index = (int)Math.floor(ratings.get(i)/STEP);
                vals[index]++;
            }
            for(int i=0;i<vals.length;i++) {
                if(vals[i] > 0){
                    values.add(new Entry(vals[i], i));
                    desc.add( i + " - " + ( i + 1 - 0.1 ) +" stars");
                }
            }

            // Legend
            pieChart.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            pieChart.setDescription(null);

            PieDataSet dataSet = new PieDataSet(values, "");
            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            dataSet.setSliceSpace(2f);

            PieData data = new PieData(desc, dataSet);
            data.setValueFormatter(new Hide0ValuesFormatter());
            data.setValueTextSize(15);
            pieChart.setData(data);
            pieChart.animateXY(1000, 1000);
        }
    }
    public static class Hide0ValuesFormatter implements ValueFormatter {
        private DecimalFormat mFormat;

        public Hide0ValuesFormatter() {
            mFormat = new DecimalFormat("###,###,##0");
        }
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value > 0) {
                return mFormat.format(value);
            } else {
                return "";
            }
        }
    }

    // Table report
    protected void initTable(TableReport tr){
        TextView wl = findViewById(R.id.wl);
        TextView fav = findViewById(R.id.fav);
        TextView total = findViewById(R.id.total);

        TextView wl_avg = findViewById(R.id.wl_avg);
        TextView fav_avg = findViewById(R.id.fav_avg);
        TextView total_avg = findViewById(R.id.total_avg);

        wl.setText(String.valueOf(tr.getWatchlist_count()));
        fav.setText(String.valueOf(tr.getFavorites_count()));
        total.setText(String.valueOf(tr.getTotal_count()));

        wl_avg.setText(String.valueOf(tr.getWatchlist_avg()));
        fav_avg.setText(String.valueOf(tr.getFavorites_avg()));
        total_avg.setText(String.valueOf(tr.getTotal_avg()));

        tableReport = tr;
    }
    protected void initTable(List<MovieWithGenres> watchlist, List<MovieWithGenres> favorites){
        TextView wl = findViewById(R.id.wl);
        TextView fav = findViewById(R.id.fav);
        TextView total = findViewById(R.id.total);

        TextView wl_avg = findViewById(R.id.wl_avg);
        TextView fav_avg = findViewById(R.id.fav_avg);
        TextView total_avg = findViewById(R.id.total_avg);

        wl.setText(String.valueOf(watchlist.size()));
        fav.setText(String.valueOf(favorites.size()));
        total.setText(String.valueOf(watchlist.size() + favorites.size()));

        float avg1 = 0;
        for(int i = 0;i<watchlist.size();i++)
            avg1 += watchlist.get(i).movie.getVote_average();
        avg1 /= watchlist.size();
        avg1 = (int)(avg1*10)/10f;
        wl_avg.setText(String.valueOf(avg1));

        float avg2 = 0;
        for(int i = 0;i<favorites.size();i++)
            avg2 += favorites.get(i).movie.getVote_average();
        avg2 /= favorites.size();
        avg2 = (int)(avg2*10)/10f;
        fav_avg.setText(String.valueOf(avg2));

        float avg3 = (watchlist.size()*avg1 + favorites.size()*avg2)/(watchlist.size() + favorites.size());
        avg3 = (int)(avg3*10)/10f;
        total_avg.setText(String.valueOf(avg3));

        // Create the TableReport object
        tableReport = new TableReport();
        tableReport.setWatchlist_count(watchlist.size());
        tableReport.setFavorites_count(favorites.size());
        tableReport.setTotal_count(watchlist.size() + favorites.size());

        tableReport.setWatchlist_avg(avg1);
        tableReport.setFavorites_avg(avg2);
        tableReport.setTotal_avg(avg3);
    }
}