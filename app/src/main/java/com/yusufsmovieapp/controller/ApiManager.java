package com.yusufsmovieapp.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.yusufsmovieapp.MoviesApplication;

import org.json.JSONObject;

@SuppressWarnings("WeakerAccess")
public class ApiManager {

    private final String MOVIES_DB_V3_API_KEY = "80ebafbad0de191a34a2260fc3dc5a8b";
    public static final String API_PREFIX = "https://api.themoviedb.org";
    public static final String IMAGE_PREFIX = "https://image.tmdb.org";
    public static final String THUMB_PREFIX = IMAGE_PREFIX + "/t/p/w185";
    public static final String BACKDROP_PREFIX = IMAGE_PREFIX + "/t/p/w780";

    private static ApiManager instance;
    private static Picasso picasso;
    private static RequestQueue queue;

    public static ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager();
        }
        queue = Volley.newRequestQueue(context.getApplicationContext());

        return instance;
    }


    public void getGenreMap(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = API_PREFIX + "/3/genre/movie/list?api_key=" + MOVIES_DB_V3_API_KEY + "&language=en-US";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
        queue.add(jsonObjReq);
    }


    public void getMostPopular(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = API_PREFIX + "/3/discover/movie?api_key=" + MOVIES_DB_V3_API_KEY + "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
        queue.add(jsonObjReq);
    }


    public void getNowPlaying(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = API_PREFIX + "/3/discover/movie?api_key=" + MOVIES_DB_V3_API_KEY + "&language=en-US&primary_release_date.gte=2018-01-01&primary_release_date.lte=2018-01-30&include_adult=false&include_video=false&page=1";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
        queue.add(jsonObjReq);
    }


    public void getMovieDetails(int id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = API_PREFIX + "/3/movie/" + id + "?api_key=" + MOVIES_DB_V3_API_KEY + "&language=en-US&append_to_response=reviews%2Ctrailers";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
        queue.add(jsonObjReq);
    }


    public static Picasso getPicasso() {
        if (picasso == null) {
            picasso = Picasso.with(MoviesApplication.getInstance());
        }
        return picasso;
    }

}