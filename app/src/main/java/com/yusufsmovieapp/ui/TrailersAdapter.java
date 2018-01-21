package com.yusufsmovieapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yusufsmovieapp.R;
import com.yusufsmovieapp.model.YouTubeTrailer;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class TrailersAdapter extends BaseAdapter {

    private YouTubeTrailer[] trailers;


    public void replaceTrailers(YouTubeTrailer[] trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trailers.length;
    }

    @Override
    public YouTubeTrailer getItem(int position) {
        return trailers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        final YouTubeTrailer trailer = getItem(position);

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.title.setText(trailer.getName());

        convertView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getSource())));
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.title) TextView title;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
