package com.marius.movies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.marius.movies.adapters.ReportsHistoryAdapter;
import com.marius.movies.data_access.AppData;
import com.marius.movies.models.ReportPair;

import java.util.ArrayList;
import java.util.List;

public class ReportsHistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<ReportPair> reports;
    ReportsHistoryAdapter adapter;
    ListView listView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_history);

        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Reports history (pairs of 2)");
        }

        reports = new ArrayList<>();
        adapter = new ReportsHistoryAdapter(this, reports);

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        progressBar = findViewById(R.id.progressBar);

        AppData.firebase_fetchReports(new AppData.onReportsFetchedListener() {
            @Override
            public void onFinished(List<ReportPair> result) {
                reports.clear();
                reports.addAll(result);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    // Back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ReportsActivity.class);
        intent.putExtra("report_pair", reports.get(position));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        reports.clear();
        progressBar.setVisibility(View.VISIBLE);
        AppData.firebase_fetchReports(new AppData.onReportsFetchedListener() {
            @Override
            public void onFinished(List<ReportPair> result) {
                reports.addAll(result);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}