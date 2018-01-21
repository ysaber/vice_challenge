package com.yusufsmovieapp.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yusufsmovieapp.MoviesApplication;
import com.yusufsmovieapp.R;
import com.yusufsmovieapp.Util;
import com.yusufsmovieapp.controller.ApiManager;
import com.yusufsmovieapp.controller.IMovieProvider;
import com.yusufsmovieapp.controller.MoviesCompiledListener;
import com.yusufsmovieapp.controller.TmdbMovieProvider;
import com.yusufsmovieapp.controller.db.MoviesDao;
import com.yusufsmovieapp.model.Movie;
import com.yusufsmovieapp.model.Review;
import com.yusufsmovieapp.model.YouTubeTrailer;

import java.util.List;

import butterknife.ButterKnife;

import static android.support.v7.graphics.Palette.Swatch;
import static android.support.v7.graphics.Palette.from;


public class MovieDetailActivity extends AppCompatActivity {

    public final static String KEY_MOVIE = "movie_key";

    final private IMovieProvider movieProvider = new TmdbMovieProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_MOVIE)) {
            final Movie movie = (Movie) extras.getSerializable(KEY_MOVIE);

            //Load the UI with whatever data we already have
            initUI(movie);

            //Download the remaining data
            loadMovieDetails(movie);
        } else {
            Toast.makeText(this, "Could not find movie!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Use the id of the Movie provided to download the full details
     *
     * @param movie Movie to download
     */
    private void loadMovieDetails(final Movie movie) {
        movieProvider.getMovieDetails(this, movie, new MoviesCompiledListener() {
            @Override
            public void onCompiled(List<Movie> movies) {
                initUI(movies.get(0));
            }

            @Override
            public void onError(String errorMessage) {
                Util.longToast(MovieDetailActivity.this, errorMessage);
            }
        });
    }



    /**
     * Initialize the UI based on the availability of the components of the Movie object
     *
     * @param movie {@link Movie} to display
     */
    private void initUI(final Movie movie) {
        ((TextView) findViewById(R.id.title)).setText(movie.getTitle());
        ((TextView) findViewById(R.id.genre)).setText(movie.getGenres());
        ((TextView) findViewById(R.id.release)).setText(movie.getReleaseDate());

        final TextView summary = findViewById(R.id.summary);
        summary.setText(movie.getSummary());
        summary.setMovementMethod(new ScrollingMovementMethod());
        summary.setScrollBarFadeDuration(999);

        final Button favsButton = findViewById(R.id.toggle_fav);
        if (movie.getIsFav() != null) {
            favsButton.setText(R.string.remove_from_favs);
        } else {
            favsButton.setText(R.string.add_to_favs);
        }

        favsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.getIsFav() != null) {
                    favsButton.setText(R.string.add_to_favs);
                    movie.setIsFav(null);
                } else {
                    favsButton.setText(R.string.remove_from_favs);
                    movie.setIsFav("TRUE");

                }
                saveMovieToDb(movie);
            }
        });

        ApiManager.getPicasso().load(ApiManager.THUMB_PREFIX + movie.getThumbnail()).into(((ImageView) findViewById(R.id.thumb)));

        final ImageView backdrop = findViewById(R.id.backdrop);

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette palette = from(bitmap).generate();
                Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    findViewById(R.id.root).setBackgroundColor(swatch.getRgb());
                }
                backdrop.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        backdrop.setTag(target);

        ApiManager.getPicasso().load(ApiManager.BACKDROP_PREFIX + movie.getBackdrop()).into(target);

        final Review[] reviews = movie.getReviews();
        if (reviews != null && reviews.length > 0) {
            final ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
            reviewsAdapter.replaceReviews(reviews);
            ((ListView) findViewById(R.id.reviews_container)).setAdapter(reviewsAdapter);
        }

        final YouTubeTrailer[] trailers = movie.getTrailers();
        if (trailers != null && trailers.length > 0) {
            final TrailersAdapter trailersAdapter = new TrailersAdapter();
            trailersAdapter.replaceTrailers(trailers);
            ((HorizontalListView) findViewById(R.id.trailers_container)).setAdapter(trailersAdapter);
        }

    }


    /**
     * Invoked from XML
     */
    public void onBackClicked(View view) {
        onBackPressed();
    }


    /**
     * This is used to make a DB call from a background thread as
     * to not hold up the UI thread.
     */
    @SuppressLint("StaticFieldLeak")
    private void saveMovieToDb(final Movie movie) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                final MoviesDao moviesDao = MoviesApplication.getInstance().getMoviesDao();
                moviesDao.insertAllWithReplace(movie);
                return null;
            }
        }.execute();
    }

}