package com.yusufsmovieapp;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

import static android.support.v7.graphics.Palette.Swatch;
import static android.support.v7.graphics.Palette.from;


public class MovieDetailActivity extends AppCompatActivity {

    public final static String KEY_MOVIE = "movie_key";

    private MoviesDao moviesDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        moviesDao = MoviesApplication.getInstance().getMoviesDao();

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
        ApiManager.getInstance(this).getMovieDetails(movie.getId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseMovieDetails(movie, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /**
     * Parse the JSONObject into a Movie object and update the UI
     */
    private void parseMovieDetails(Movie movie, JSONObject response) {
        try {

            final Gson gson = new Gson();

            final JSONArray reviewsJsonArray = response.getJSONObject("reviews").getJSONArray("results");
            final JSONArray trailersJsonArray = response.getJSONObject("trailers").getJSONArray("youtube");

            final Review [] reviews = new Review[reviewsJsonArray.length()];
            final YouTubeTrailer [] trailers = new YouTubeTrailer[trailersJsonArray.length()];

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
            initUI(movie);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        final Review [] reviews = movie.getReviews();
        if (reviews != null && reviews.length > 0) {
            final ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
            reviewsAdapter.replaceReviews(reviews);
            ((ListView) findViewById(R.id.reviews_container)).setAdapter(reviewsAdapter);
        }

        final YouTubeTrailer [] trailers = movie.getTrailers();
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
                moviesDao.insertAllWithReplace(movie);
                return null;
            }
        }.execute();
    }

}