package com.marius.movies.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.marius.movies.R;
import com.marius.movies.models.ReportPair;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReportsHistoryAdapter extends ArrayAdapter<ReportPair> {
    Context context;
    List<ReportPair> reports;

    LayoutInflater inflater;

    public ReportsHistoryAdapter(@NonNull Context context, @NonNull List<ReportPair> objects) {
        super(context, 0, objects);
        this.context = context;
        this.reports = objects;

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reports.size();
    }
    @Nullable @Override
    public ReportPair getItem(int position) {
        return reports.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.reports_history_item, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.friendly_name);
            holder.date = (TextView) convertView.findViewById(R.id.creation_date);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ReportPair r = reports.get(position);
        holder.name.setText(r.getFriendlyName());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(sdf.format(r.getCreationDate()));

        return convertView;
    }



    public static class ViewHolder {
        public TextView name;
        public TextView date;
    }
}
