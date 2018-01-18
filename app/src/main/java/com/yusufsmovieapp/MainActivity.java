package com.yusufsmovieapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    private MoviesDao moviesDao;

    private MoviesAdapter moviesAdapter;
    private MoviesAdapter favsAdapter;

    @BindView(R.id.list)
    ListView listView;

    @BindView(R.id.now_playing)
    View nowPlayingContainer;

    @BindView(R.id.now_playing_label)
    View nowPlayingLabel;

    @BindView(R.id.movies_label)
    TextView moviesLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        moviesDao = ((MoviesApplication) getApplication()).getMoviesDao();

        initNavListener();

        moviesAdapter = new MoviesAdapter(this);
        favsAdapter = new MoviesAdapter(this);
        listView.setAdapter(moviesAdapter);

        if (isOnline()) {
            ApiManager.getInstance(this).getGenreMap(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    parseAndSaveGenres(response);
                    getNowPlaying();
                    getMostPopular();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } else {
            Toast.makeText(this, "You need an internet connection to load movies", Toast.LENGTH_LONG).show();
        }
    }

    private void parseAndSaveGenres(JSONObject response) {
        try {
            final JSONArray genresJsonArray = response.getJSONArray("genres");
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < genresJsonArray.length(); i++) {
                genres.add(new Gson().fromJson(genresJsonArray.get(i).toString(), Genre.class));
            }
            MoviesApplication.getInstance().setGenres(genres);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //We call getFavs here and not in onCreate so that it can be invoked when a Movie detail
        //screen is closed. This way, the favourites list is constantly being refreshed and the
        //latest list is always showing.
        getFavs();
    }


    /**
     * Check if the device has an active internet connection.
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     * Set up the navigation for the app. This consists of the bottom menu which has
     * 2 buttons: home, and favourites.
     */
    private void initNavListener() {
        final OnNavigationItemSelectedListener navListener = new OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navigation_home: {
                        listView.setAdapter(moviesAdapter);
                        nowPlayingContainer.setVisibility(View.VISIBLE);
                        nowPlayingLabel.setVisibility(View.VISIBLE);
                        moviesLabel.setText(R.string.all_movies);
                        return true;
                    }

                    case R.id.navigation_favs: {
                        getFavs();
                        listView.setAdapter(favsAdapter);
                        nowPlayingContainer.setVisibility(View.GONE);
                        nowPlayingLabel.setVisibility(View.GONE);
                        moviesLabel.setText(R.string.menu_favs);
                        return true;
                    }

                    default: {
                        return false;
                    }
                }
            }
        };

        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(navListener);
    }


    /**
     * Download the list of 'Now Playing' movies
     */
    private void getNowPlaying() {
        ApiManager.getInstance(this).getNowPlaying(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseNowPlaying(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Could not load movies due to: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Parse the list of 'Now Playing' movies
     *
     * @param response JSONObject to parse
     */
    private void parseNowPlaying(JSONObject response) {
        try {
            final List<Movie> movies = new ArrayList<>();
            final Gson gson = new Gson();
            final JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                final JSONObject movie = results.getJSONObject(i);
                final Movie movieObj = gson.fromJson(movie.toString(), Movie.class);
                movieObj.setGenres(MoviesApplication.getInstance().getGenreString(gson.fromJson(movie.get("genre_ids").toString(), int[].class)));
                saveMovieToDb(movieObj);
                movies.add(movieObj);
            }

            final MoviesAdapter nowPlayingAdapter = new MoviesAdapter(MainActivity.this);
            nowPlayingAdapter.setShowMinVersion();
            nowPlayingAdapter.replaceMovies(movies);
            ((HorizontalListView) findViewById(R.id.now_playing)).setAdapter(nowPlayingAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is used to download the list of most popular movies from the TMDB API
     * and invoke the parser {@link #parseMostPopular(JSONObject)}. If an error is found,
     * a toast is shown.
     */
    private void getMostPopular() {
        ApiManager.getInstance(this).getMostPopular(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseMostPopular(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Could not load movies due to: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * This method parses the JSONObject pulled from the TMDB API for the 'most popular' API.
     *
     * @param response JSONObject to be parsed
     */
    private void parseMostPopular(JSONObject response) {
        try {
            final List<Movie> movies = new ArrayList<>();
            final Gson gson = new Gson();
            final JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                final JSONObject movie = results.getJSONObject(i);
                final Movie movieObj = gson.fromJson(movie.toString(), Movie.class);
                movieObj.setGenres(MoviesApplication.getInstance().getGenreString(gson.fromJson(movie.get("genre_ids").toString(), int[].class)));
                saveMovieToDb(movieObj);
                movies.add(movieObj);
            }

            moviesAdapter.replaceMovies(movies);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads the list of favourite movies from the DB
     */
    @SuppressLint("StaticFieldLeak")
    private void getFavs() {

        new AsyncTask<Void, Void, List<Movie>>() {

            @Override
            protected List<Movie> doInBackground(Void... voids) {
                return moviesDao.getFavs();
            }

            @Override
            protected void onPostExecute(List<Movie> movies) {
                super.onPostExecute(movies);
                favsAdapter.replaceMovies(movies);
            }
        }.execute();
    }


    /**
     * Saves a particular moview to the DB. Required to be in an AsyncTask
     * to avoid bottling up the UI thread.
     *
     * @param movie Movie to be saved
     */
    @SuppressLint("StaticFieldLeak")
    private void saveMovieToDb(final Movie movie) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                moviesDao.insertAllWithIgnore(movie);
                return null;
            }
        }.execute();

    }


}