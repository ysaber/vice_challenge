package com.yusufsmovieapp.controller;

import com.yusufsmovieapp.model.Movie;

import java.util.List;

/**
 * Interface to pass when a list of movies is compiled
 */
public interface MoviesCompiledListener {

    void onCompiled(List<Movie> movies);

    void onError(String errorMessage);

}