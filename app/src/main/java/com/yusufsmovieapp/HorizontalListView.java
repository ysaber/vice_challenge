package com.yusufsmovieapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * This is a custom view that extends {@link HorizontalScrollView} that is designed to behave
 * like a horizontal version of  {@link android.widget.ListView}, hence the name.
 * It contains two methods that are familiar to those who are familiar with ListView:
 * {@link #setAdapter(BaseAdapter)} and {@link #notifyDataSetChanged()}.
 */
public class HorizontalListView extends HorizontalScrollView {

    private LinearLayout linearLayout;
    private BaseAdapter adapter;


    public HorizontalListView(Context context) {
        super(context);
        init(context);
    }


    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public HorizontalListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setHorizontalScrollBarEnabled(false);
        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(linearLayout);
    }


    /**
     * Load the HorizontalListView with data. The BaseAdapter provided should be designed
     * as if it were being used by a standard {@link android.widget.ListView}.
     *
     * @param adapter BaseAdapter to be used
     */
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        notifyDataSetChanged();
    }


    /**
     * Designed to perform a similar behaviour of {@link BaseAdapter#notifyDataSetChanged()}.
     * This method essentially refreshes the HorizontalListView based on the data in the adapter
     * that is set using {@link #setAdapter(BaseAdapter)}.
     */
    public void notifyDataSetChanged() {
        linearLayout.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            linearLayout.addView(adapter.getView(i, null, this));
        }
        linearLayout.invalidate();
    }

}
