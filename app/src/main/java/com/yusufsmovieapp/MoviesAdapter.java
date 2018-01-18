package com.yusufsmovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class MoviesAdapter extends BaseAdapter {

    private List<Movie> movies = new ArrayList<>();
    private int screenWidth;
    private boolean showMinVersion;


    public MoviesAdapter(Activity activity) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
    }


    public void setShowMinVersion() {
        showMinVersion = true;
    }


    public void replaceMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return movies.size();
    }


    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final Context context = parent.getContext();
        final Movie movie = getItem(position);


        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }


        if (showMinVersion) {
            holder.root.getLayoutParams().width = (int) (screenWidth * 0.9);
        }
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenres());

        final ImageView thumb = holder.thumb;

        ApiManager.getPicasso().load(ApiManager.THUMB_PREFIX + movie.getThumbnail()).into(thumb);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.KEY_MOVIE, movie);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, thumb, ViewCompat.getTransitionName(thumb));
                context.startActivity(intent, options.toBundle());
            }
        });

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.genre) TextView genre;
        @BindView(R.id.root) View root;
        @BindView(R.id.thumb) ImageView thumb;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}