package com.marius.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.marius.movies.R;
import com.marius.movies.models.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreCheckboxAdapter extends ArrayAdapter<GenreCheckboxAdapter.CheckableGenre> {
    Context context;
    List<GenreCheckboxAdapter.CheckableGenre> genres;

    LayoutInflater inflater;
    boolean fromInit;

    public GenreCheckboxAdapter(@NonNull Context context, int resource, @NonNull List<GenreCheckboxAdapter.CheckableGenre> objects) {
        super(context, resource, objects);

        this.context = context;
        this.genres = objects;

        inflater = LayoutInflater.from(context);
        fromInit = false;
    }

    @Override
    public int getCount() { return genres.size(); }
    @Override
    public long getItemId(int position) { return position; }
    @Nullable @Override
    public CheckableGenre getItem(int position) { return genres.get(position); }

    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.genre_spinner_item, parent, false);

            holder = new ViewHolder();
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.genre_item_checkbox);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CheckableGenre g = genres.get(position);
        holder.checkbox.setText(g.genre.getName());

        fromInit = true;
        holder.checkbox.setChecked(g.isChecked);
        fromInit = false;

        holder.checkbox.setTag(position);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (Integer) buttonView.getTag();

                if (!fromInit) {
                    genres.get(pos).isChecked = isChecked;
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public CheckBox checkbox;
    }
    public static class CheckableGenre {
        public Genre genre;
        public boolean isChecked;

        public CheckableGenre(Genre g){
            genre = g;
            isChecked = false;
        }
    }
}
