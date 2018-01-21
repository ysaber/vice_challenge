package com.yusufsmovieapp.controller.db;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.yusufsmovieapp.model.Movie;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM movies WHERE fav is 'TRUE'")
    List<Movie> getFavs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllWithReplace(Movie... users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllWithIgnore(Movie... users);

}