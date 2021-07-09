package com.marius.movies.models;

import java.io.Serializable;

public class TableReport implements Serializable {
    int watchlist_count;
    int favorites_count;
    int total_count;

    float watchlist_avg;
    float favorites_avg;
    float total_avg;

    public int getWatchlist_count() {
        return watchlist_count;
    }
    public void setWatchlist_count(int watchlist_count) {
        this.watchlist_count = watchlist_count;
    }

    public int getFavorites_count() {
        return favorites_count;
    }
    public void setFavorites_count(int favorites_count) {
        this.favorites_count = favorites_count;
    }

    public int getTotal_count() {
        return total_count;
    }
    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public float getWatchlist_avg() {
        return watchlist_avg;
    }
    public void setWatchlist_avg(float watchlist_avg) {
        this.watchlist_avg = watchlist_avg;
    }

    public float getFavorites_avg() {
        return favorites_avg;
    }
    public void setFavorites_avg(float favorites_avg) {
        this.favorites_avg = favorites_avg;
    }

    public float getTotal_avg() {
        return total_avg;
    }
    public void setTotal_avg(float total_avg) {
        this.total_avg = total_avg;
    }
}
