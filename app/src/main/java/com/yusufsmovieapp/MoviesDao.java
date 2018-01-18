package com.yusufsmovieapp;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    @Query("SELECT * FROM movies WHERE id IN (:ids)")
    List<Movie> loadAllByIds(int[] ids);

    @Query("SELECT * FROM movies WHERE original_title LIKE :title LIMIT 1")
    Movie findByTitle(String title);

    @Query("SELECT * FROM movies WHERE fav is 'TRUE'")
    List<Movie> getFavs();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllWithReplace(Movie... users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllWithIgnore(Movie... users);

    @Delete
    void delete(Movie user);

}
