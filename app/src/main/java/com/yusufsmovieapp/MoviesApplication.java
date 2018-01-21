package com.yusufsmovieapp;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.yusufsmovieapp.controller.db.AppDatabase;
import com.yusufsmovieapp.controller.db.MoviesDao;
import com.yusufsmovieapp.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class MoviesApplication extends Application {

    private static MoviesApplication instance;

    private MoviesDao moviesDao;
    private List<Genre> genres = new ArrayList<>();


    public static MoviesApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "moviesdb").build();
        moviesDao = db.getDao();
    }


    public MoviesDao getMoviesDao() {
        return moviesDao;
    }

    public void setGenres(List<Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
    }

    public Genre getGenreById(int id) {
        for (Genre genre : genres) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        return null;
    }

    public String getGenreString(int [] ids) {
        if (ids != null && ids.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < ids.length; i++) {
                final Genre genre = getGenreById(ids[i]);
                if (genre != null) {
                    stringBuilder.append(genre.getName());
                    if (i + 1 < ids.length) {
                        stringBuilder.append(", ");
                    }
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }
}
