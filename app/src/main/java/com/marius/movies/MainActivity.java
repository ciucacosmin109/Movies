package com.marius.movies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marius.movies.data_access.AppData;
import com.marius.movies.fragments.FavoritesFragment;
import com.marius.movies.fragments.SearchFragment;
import com.marius.movies.fragments.WatchlistFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_CODE_SETTINGS_UPDATED = 300;

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bottom navigation setup from prefs
        bottomNav = findViewById(R.id.bottom_navigation);

        // If it is the first start get the fragment from prefs
        if(savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);
            String str = prefs.getString("startup", "search");
            if (str != null) {
                switch (str) {
                    case "watchlist":
                        bottomNav.setSelectedItemId(R.id.nav_watchlist);
                        changeFragment(R.id.nav_watchlist);
                        break;
                    case "favorites":
                        bottomNav.setSelectedItemId(R.id.nav_favorites);
                        changeFragment(R.id.nav_favorites);
                        break;
                    default:
                        bottomNav.setSelectedItemId(R.id.nav_search);
                        changeFragment(R.id.nav_search);
                        break;
                }
            } else {
                bottomNav.setSelectedItemId(R.id.nav_search);
                changeFragment(R.id.nav_search);
            }
        }
        bottomNav.setOnNavigationItemSelectedListener(this);

        // Fetch all genres
        AppData.smart_fetchAllGenres(this, null);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.options:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_SETTINGS_UPDATED);
                return true;
            case R.id.reports:
                startActivity(new Intent(this, ReportsActivity.class));
                return true;
            case R.id.reports_history:
                startActivity(new Intent(this, ReportsHistoryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Handle rotation = save the fragment
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragment", bottomNav.getSelectedItemId());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        changeFragment(savedInstanceState.getInt("fragment"));
    }

    // Change the fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        changeFragment(item.getItemId());
        return true;
    }
    void changeFragment(int id){
        Fragment selectedFrag;
        String tag;

        switch(id){
            case R.id.nav_watchlist:
                tag = "watchlist";
                selectedFrag = getSupportFragmentManager().findFragmentByTag(tag);
                if(selectedFrag == null)
                    selectedFrag = new WatchlistFragment();
                getSupportActionBar().setTitle(R.string.watchlist);
                break;
            case R.id.nav_favorites:
                tag = "favorites";
                selectedFrag = getSupportFragmentManager().findFragmentByTag(tag);
                if(selectedFrag == null)
                    selectedFrag = new FavoritesFragment();
                getSupportActionBar().setTitle(R.string.favorites);
                break;
            case R.id.nav_search:
                tag = "search";
                selectedFrag = getSupportFragmentManager().findFragmentByTag(tag);
                if(selectedFrag == null)
                    selectedFrag = new SearchFragment();
                getSupportActionBar().setTitle(R.string.search);
                break;
            default: return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFrag, tag)
                .commit();
    }

}