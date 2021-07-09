package com.marius.movies.data_access;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marius.movies.fetchers.GenreFetcher;
import com.marius.movies.models.Genre;
import com.marius.movies.models.Movie;
import com.marius.movies.models.MovieGenreCrossRef;
import com.marius.movies.models.MovieWithGenres;
import com.marius.movies.models.ReportPair;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    // Global data
    public static List<MovieWithGenres> watchlistMovies = new ArrayList<>();
    public static List<MovieWithGenres> favoritesMovies = new ArrayList<>();

    public static List<Genre> allGenres;

    // Movies
    public static void room_fetchMovieWithGenres(final Activity context, final Movie.MOVIE_LIST movie_list, final onMoviesFetchedListener delegate){
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<MovieWithGenres> movies = db.getCrossRefDao().getAllMoviesWithGenres(movie_list);

                // Update the UI
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(delegate != null)
                            delegate.onFinished(movies);
                    }
                });
            }
        });
    }
    public static void room_insertMovieWithGenres(final Activity context, final MovieWithGenres mwg){
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                long rowId = db.getMovieDao().insert(mwg.movie);
                int id = db.getMovieDao().getNewId(rowId);
                mwg.movie.setId(id);

                if(mwg.genres != null) {
                    for (int i = 0; i < mwg.genres.size(); i++) {
                        int gId = mwg.genres.get(i).getId();

                        rowId = db.getGenreDao().insert(mwg.genres.get(i));
                        int dbGrId = db.getGenreDao().getNewId(rowId);
                        if (gId == 0)
                            gId = dbGrId;

                        System.out.println(id + "------" + gId);

                        MovieGenreCrossRef cr = new MovieGenreCrossRef();
                        cr.movie_id = id;
                        cr.genre_id = gId;
                        db.getCrossRefDao().insert(cr);
                    }
                }
            }
        });
    }
    public static void room_deleteMovieWithGenres(final Activity context, final MovieWithGenres mwg){
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.getCrossRefDao().deleteMoviesWithGenres(mwg.movie, mwg.genres);
            }
        });
    }
    public static void room_updateMovieWithGenres(final Activity context, final MovieWithGenres mwg) {
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                db.getMovieDao().update(mwg.movie);
                int id = mwg.movie.getId();
                db.getCrossRefDao().delete(id);

                if(mwg.genres != null) {
                    for (int i = 0; i < mwg.genres.size(); i++) {
                        int gId = mwg.genres.get(i).getId();

                        long rowId = db.getGenreDao().insert(mwg.genres.get(i));
                        int dbGrId = db.getGenreDao().getNewId(rowId);
                        if (gId == 0)
                            gId = dbGrId;

                        MovieGenreCrossRef cr = new MovieGenreCrossRef();
                        cr.movie_id = id;
                        cr.genre_id = gId;
                        db.getCrossRefDao().insert(cr);
                    }
                }
            }
        });
    }

    // Genres
    public static void smart_fetchAllGenres(final Activity context, final onGenresFetchedListener delegate){
        // If I fetched the genres from the API, return them
        if(GenreFetcher.lastGenres != null){
            allGenres = GenreFetcher.lastGenres;
            room_insertAllGenres(context, allGenres);
            if(delegate != null)
                delegate.onFinished(allGenres);
            return;
        }

        // Try to fetch the genres from the API
        GenreFetcher fetcher = new GenreFetcher(new GenreFetcher.onGenreFetchFinishedListener() {
            @Override
            public void onGenreFetchFinished(List<Genre> result) {
                if(result != null && result.size() > 0){
                    allGenres = result;
                    room_insertAllGenres(context, allGenres);
                    if(delegate != null)
                        delegate.onFinished(result);
                    return;
                }

                // Try to get the genres from the local DB
                room_fetchAllGenres(context, new onGenresFetchedListener() {
                    @Override
                    public void onFinished(List<Genre> result) {
                        if(result != null && result.size() > 0) {
                            allGenres = result;
                            room_insertAllGenres(context, allGenres);
                            if (delegate != null)
                                delegate.onFinished(result);
                        }else{
                            if (delegate != null)
                                delegate.onFinished(allGenres);
                        }
                    }
                });
            }
        });
        fetcher.execute();

    }
    public static void room_fetchAllGenres(final Activity context, final onGenresFetchedListener delegate){
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Genre> res = db.getGenreDao().getAll();

                // Update the UI
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(delegate != null)
                            delegate.onFinished(res);
                    }
                });
            }
        });
    }
    public static void room_insertAllGenres(final Activity context, final List<Genre> genres){
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < genres.size(); i++) {
                    db.getGenreDao().insert(genres.get(i));
                }
            }
        });
    }

    // Reports (firebase)
    public static String firebase_insertReport(ReportPair report){
        DatabaseReference db = getFirebaseDbInstance();

        String id = db.push().getKey();
        report.setFirebase_id(id);
        if(id != null)
            db.child("reports").child(id).setValue(report);

        return id;
    }
    public static void firebase_fetchReports(final onReportsFetchedListener delegate){
        DatabaseReference db = getFirebaseDbInstance();

        db.child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReportPair> list = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ReportPair r = snap.getValue(ReportPair.class);
                    if(r != null) {
                        r.setFirebase_id(snap.getKey());
                        list.add(r);
                    }
                }
                if(delegate != null)
                    delegate.onFinished(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FIREBASE_CANCEL", "loadPost:onCancelled", error.toException());
            }
        });
    }
    public static void firebase_deleteReport(String id){
        DatabaseReference db = getFirebaseDbInstance();
        db.child("reports").child(id).removeValue();
    }


    // Singleton for firebase
    private static final Object LOCK = new Object();
    private static DatabaseReference firebaseInstance;
    public static DatabaseReference getFirebaseDbInstance(){
        if(firebaseInstance != null)
            return firebaseInstance;

        synchronized (LOCK){
            if(firebaseInstance == null){
                firebaseInstance = FirebaseDatabase.getInstance().getReference();
            }
        }
        return firebaseInstance;
    }

    // Functional interface
    public interface onMoviesFetchedListener{
        void onFinished(List<MovieWithGenres> result);
    }
    public interface onGenresFetchedListener{
        void onFinished(List<Genre> result);
    }
    public interface onReportsFetchedListener{
        void onFinished(List<ReportPair> result);
    }
}
