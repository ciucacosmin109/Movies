package com.marius.movies;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }
    }
    // Back button
    @Override
    public boolean onSupportNavigateUp() { 
        finish();
        return true;
    }

    // Save prefs
    @Override
    protected void onPause() {
        SharedPreferences uiPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Save prefs
        SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("startup", uiPrefs.getString("startup", "search"));
        edit.putString("nr_of_genres", uiPrefs.getString("nr_of_genres", "3"));
        edit.putBoolean("adult", uiPrefs.getBoolean("adult", true));
        edit.commit();

        super.onPause();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

    }
}