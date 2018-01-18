package com.yusufsmovieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class ReviewsAdapter extends BaseAdapter {

    private Review [] reviews;


    public void replaceReviews(Review [] reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public Review getItem(int position) {
        return reviews[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        final Review review = getItem(position);

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.author.setText(review.getAuthor());
        holder.review.setText(review.getContent());

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.author) TextView author;
        @BindView(R.id.review) TextView review;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
