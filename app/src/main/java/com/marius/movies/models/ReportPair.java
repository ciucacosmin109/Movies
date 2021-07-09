package com.marius.movies.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportPair implements Serializable {
    String firebase_id;
    List<Float> pie;
    TableReport table;
    String friendlyName;
    Date creationDate;

    public ReportPair() { }
    public ReportPair(String firebase_id, List<Float> pie, TableReport table, String friendlyName) {
        this.firebase_id = firebase_id;

        this.pie = pie;
        this.table = table;
        this.friendlyName = friendlyName;

        this.creationDate = new Date(System.currentTimeMillis());
    }
    public ReportPair(List<Float> pie, TableReport table, String friendlyName) {
        this.pie = pie;
        this.table = table;
        this.friendlyName = friendlyName;

        this.creationDate = new Date(System.currentTimeMillis());
    }

    public String getFirebase_id() {
        return firebase_id;
    }
    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }

    public List<Float> getPie() {
        return pie;
    }
    public void setPie(List<Float> pie) {
        this.pie = pie;
    }

    public TableReport getTable() {
        return table;
    }
    public void setTable(TableReport table) {
        this.table = table;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
