package com.yusufsmovieapp;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DbManager {

    private Context context;
    private AppDatabase db;
    private MoviesDao moviesDao;

    public DbManager(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "moviesdb").build();
        moviesDao = db.getDao();
    }

    public MoviesDao getMoviesDao(Context context) {
        return moviesDao;
    }
}
