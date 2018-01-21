package com.yusufsmovieapp.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yusufsmovieapp.MoviesApplication;
import com.yusufsmovieapp.controller.db.MoviesDao;
import com.yusufsmovieapp.model.Genre;
import com.yusufsmovieapp.model.Movie;
import com.yusufsmovieapp.model.Review;
import com.yusufsmovieapp.model.YouTubeTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link IMovieProvider} for getting the list of movies from TMDB
 */
public class TmdbMovieProvider implements IMovieProvider {


    @Override
    public void getGenreMap(Context context, final GenreCompiledListener genreCompiledListener) {
        ApiManager.getInstance(context).getGenreMap(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                genreCompiledListener.onCompiled(parseAndSaveGenres(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                genreCompiledListener.onError(error.getLocalizedMessage());
            }
        });
    }



    @Override
    public void getNowPlaying(final Context context, final MoviesCompiledListener moviesCompiledListener) {
        ApiManager.getInstance(context).getNowPlaying(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final List<Movie> movies = parseNowPlaying(context, response);
                moviesCompiledListener.onCompiled(movies);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                moviesCompiledListener.onError(error.getLocalizedMessage());
            }
        });
    }


    @Override
    public void getMostPopular(final Context context, final MoviesCompiledListener moviesCompiledListener) {
        ApiManager.getInstance(context).getMostPopular(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final List<Movie> movies = parseMostPopular(context, response);
                moviesCompiledListener.onCompiled(movies);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                moviesCompiledListener.onError(error.getLocalizedMessage());
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void getFavourites(final Context context, final MoviesCompiledListener moviesCompiledListener) {
        new AsyncTask<Void, Void, List<Movie>>() {

            @Override
            protected List<Movie> doInBackground(Void... voids) {
                final MoviesDao moviesDao = ((MoviesApplication) context.getApplicationContext()).getMoviesDao();
                return moviesDao.getFavs();
            }

            @Override
            protected void onPostExecute(List<Movie> movies) {
                super.onPostExecute(movies);
                moviesCompiledListener.onCompiled(movies);
            }
        }.execute();
    }

    @Override
    public void getMovieDetails(final Context context, final Movie movie, final MoviesCompiledListener moviesCompiledListener) {
        ApiManager.getInstance(context).getMovieDetails(movie.getId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final List<Movie> movies = Collections.singletonList(parseMovieDetails(context, movie, response));
                moviesCompiledListener.onCompiled(movies);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    private List<Genre> parseAndSaveGenres(JSONObject response) {
        List<Genre> genres = new ArrayList<>();
        try {
            final JSONArray genresJsonArray = response.getJSONArray("genres");
            for (int i = 0; i < genresJsonArray.length(); i++) {
                genres.add(new Gson().fromJson(genresJsonArray.get(i).toString(), Genre.class));
            }

        } catch (JSONException e) {
            //TODO HANDLE ME
        }
        return genres;
    }


    /**
     * Parse the list of 'Now Playing' movies
     *
     * @param response JSONObject to parse
     */
    private List<Movie> parseNowPlaying(Context context, JSONObject response) {

        final List<Movie> movies = new ArrayList<>();

        try {
            final Gson gson = new Gson();
            final JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                final JSONObject movie = results.getJSONObject(i);
                final Movie movieObj = gson.fromJson(movie.toString(), Movie.class);
                movieObj.setGenres(MoviesApplication.getInstance().getGenreString(gson.fromJson(movie.get("genre_ids").toString(), int[].class)));
                movies.add(movieObj);
                saveMovieToDb(context, movieObj);
            }

        } catch (JSONException e) {
            //TODO HANDLE ME
        }


        return movies;

    }


    /**
     * This method parses the JSONObject pulled from the TMDB API for the 'most popular' API.
     *
     * @param response JSONObject to be parsed
     */
    private List<Movie> parseMostPopular(Context context, JSONObject response) {

        final List<Movie> movies = new ArrayList<>();

        try {
            final Gson gson = new Gson();
            final JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                final JSONObject movie = results.getJSONObject(i);
                final Movie movieObj = gson.fromJson(movie.toString(), Movie.class);
                movieObj.setGenres(MoviesApplication.getInstance().getGenreString(gson.fromJson(movie.get("genre_ids").toString(), int[].class)));
                saveMovieToDb(context, movieObj);
                movies.add(movieObj);
            }

        } catch (JSONException e) {
            //TODO HANDLE ME
        }

        return  movies;
    }


    /**
     * Parse the JSONObject into a Movie object and update the UI
     */
    private Movie parseMovieDetails(Context context, Movie movie, JSONObject response) {
        try {

            final Gson gson = new Gson();

            final JSONArray reviewsJsonArray = response.getJSONObject("reviews").getJSONArray("results");
            final JSONArray trailersJsonArray = response.getJSONObject("trailers").getJSONArray("youtube");

            final Review[] reviews = new Review[reviewsJsonArray.length()];
            final YouTubeTrailer[] trailers = new YouTubeTrailer[trailersJsonArray.length()];

            for (int i = 0; i < reviewsJsonArray.length(); i++) {
                Review review = gson.fromJson(reviewsJsonArray.get(i).toString(), Review.class);
                reviews[i] = review;
            }

            for (int i = 0; i < trailersJsonArray.length(); i++) {
                YouTubeTrailer trailer = gson.fromJson(trailersJsonArray.get(i).toString(), YouTubeTrailer.class);
                trailers[i] = trailer;
            }

            movie.setReviews(reviews);
            movie.setTrailers(trailers);

            saveMovieToDb(context, movie);

            return movie;

        } catch (JSONException e) {
            return null;
        }
    }



    /**
     * Saves a particular moview to the DB. Required to be in an AsyncTask
     * to avoid bottling up the UI thread.
     *
     * @param movie Movie to be saved
     */
    @SuppressLint("StaticFieldLeak")
    private void saveMovieToDb(final Context context, final Movie movie) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                final MoviesDao moviesDao = ((MoviesApplication) context.getApplicationContext()).getMoviesDao();
                moviesDao.insertAllWithIgnore(movie);
                return null;
            }
        }.execute();

    }

}
