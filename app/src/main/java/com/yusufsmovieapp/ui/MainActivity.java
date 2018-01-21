package com.yusufsmovieapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yusufsmovieapp.model.Genre;
import com.yusufsmovieapp.controller.GenreCompiledListener;
import com.yusufsmovieapp.controller.IMovieProvider;
import com.yusufsmovieapp.model.Movie;
import com.yusufsmovieapp.MoviesApplication;
import com.yusufsmovieapp.controller.MoviesCompiledListener;
import com.yusufsmovieapp.R;
import com.yusufsmovieapp.controller.TmdbMovieProvider;
import com.yusufsmovieapp.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    final private IMovieProvider movieProvider = new TmdbMovieProvider();

    private MoviesAdapter moviesAdapter;
    private MoviesAdapter favouritesAdapter;

    @BindView(R.id.list) ListView listView;
    @BindView(R.id.now_playing)
    HorizontalListView nowPlayingContainer;
    @BindView(R.id.now_playing_label) View nowPlayingLabel;
    @BindView(R.id.movies_label) TextView moviesLabel;


    final private GenreCompiledListener genreCompiledListener = new GenreCompiledListener() {
        @Override
        public void onCompiled(List<Genre> genres) {
            MoviesApplication.getInstance().setGenres(genres);
            movieProvider.getNowPlaying(MainActivity.this, nowPlayingCompiledListener);
            movieProvider.getMostPopular(MainActivity.this, mostPopularCompiledListener);
        }

        @Override
        public void onError(String errorMessage) {
            Util.longToast(MainActivity.this, errorMessage);
        }
    };


    final private MoviesCompiledListener nowPlayingCompiledListener = new MoviesCompiledListener() {
        @Override
        public void onCompiled(List<Movie> movies) {
            final MoviesAdapter nowPlayingAdapter = new MoviesAdapter(MainActivity.this);
            nowPlayingAdapter.setShowMinVersion();
            nowPlayingAdapter.replaceMovies(movies);
            nowPlayingContainer.setAdapter(nowPlayingAdapter);
        }

        @Override public void onError(String errorMessage) {
            Util.longToast(MainActivity.this, errorMessage);
        }
    };


    final private MoviesCompiledListener mostPopularCompiledListener = new MoviesCompiledListener() {
        @Override
        public void onCompiled(List<Movie> movies) {
            moviesAdapter.replaceMovies(movies);
        }

        @Override
        public void onError(String errorMessage) {
            Util.longToast(MainActivity.this, errorMessage);
        }
    };


    final private MoviesCompiledListener favouritesCompiledListener =  new MoviesCompiledListener() {
        @Override
        public void onCompiled(List<Movie> movies) {
            favouritesAdapter.replaceMovies(movies);
        }

        @Override
        public void onError(String errorMessage) {
            Util.longToast(MainActivity.this, errorMessage);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initNavListener();

        moviesAdapter = new MoviesAdapter(this);
        favouritesAdapter = new MoviesAdapter(this);
        listView.setAdapter(moviesAdapter);

        if (Util.isOnline()) {
            movieProvider.getGenreMap(this, genreCompiledListener);
        } else {
            Util.longToast(this, "You need an internet connection to load movies");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //We call getFavourites here and not in onCreate so that it can be invoked when a Movie detail
        //screen is closed. This way, the favourites list is never outdated.
        movieProvider.getFavourites(this, favouritesCompiledListener);
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
                        movieProvider.getFavourites(MainActivity.this, favouritesCompiledListener);
                        listView.setAdapter(favouritesAdapter);
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

}