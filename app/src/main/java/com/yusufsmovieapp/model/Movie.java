package com.yusufsmovieapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "movies")
public class Movie implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    private String title;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String thumbnail;

    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    private String backdrop;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    @ColumnInfo(name = "genres")
    private String genres;

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    private String summary;

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private String rating;

    @ColumnInfo(name = "fav")
    private String isFav;

    @Ignore
    private Review[] reviews;

    @Ignore
    private YouTubeTrailer[] trailers;


    public Movie() { }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getGenres() {
        return genres;
    }

    public String getSummary() {
        return summary;
    }

    public String getRating() {
        return rating;
    }

    public Review[] getReviews() {
        return reviews;
    }

    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
    }

    public YouTubeTrailer[] getTrailers() {
        return trailers;
    }

    public void setTrailers(YouTubeTrailer[] trailers) {
        this.trailers = trailers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }
}