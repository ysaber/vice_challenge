package com.yusufsmovieapp.controller;

import android.content.Context;

import com.yusufsmovieapp.model.Movie;

/**
 * An interface for getting lists of movies from a source.
 */
public interface IMovieProvider {


    /**
     * Get the list of genres to be used for the movies UI.
     */
    void getGenreMap(Context context, GenreCompiledListener genreCompiledListener);


    /**
     * Get the list of movies currently playing in theaters.
     */
    void getNowPlaying(Context context, MoviesCompiledListener moviesCompiledListener);


    /**
     * Get the list of most popular movies.
     */
    void getMostPopular(Context context, MoviesCompiledListener moviesCompiledListener);


    /**
     * Get the list of the user's favourite movies.
     */
    void getFavourites(Context context, MoviesCompiledListener moviesCompiledListener);


    /**
     * Get the details of a specific movie.
     */
    void getMovieDetails(Context context, Movie movie, MoviesCompiledListener moviesCompiledListener);
}