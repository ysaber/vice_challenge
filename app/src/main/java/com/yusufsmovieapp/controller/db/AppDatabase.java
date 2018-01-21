package com.yusufsmovieapp.controller.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.yusufsmovieapp.model.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MoviesDao getDao();
}