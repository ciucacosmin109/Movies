package com.marius.movies.data_access;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static AppExecutors instance;
    private final Executor diskIO;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance(){
        if(instance != null)
            return instance;

        synchronized (LOCK){
            if (instance == null) {
                instance = new AppExecutors(
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3)
                );
            }
        }
        return instance;
    }

    public Executor getDiskIO() { return diskIO; }
    public Executor getNetworkIO() { return networkIO; }

}
