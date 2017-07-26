package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lilla on 26/07/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.sectionName) TextView sectionNameView;

    public NewsAdapter(Context context, List<News> news)  {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
                    ButterKnife.bind(this, listItemView);
        }

        News currentNews = getItem(position);

        titleView.setText(currentNews.getTitle());
        sectionNameView.setText(currentNews.getSectionName());

        return listItemView;
    }
}
