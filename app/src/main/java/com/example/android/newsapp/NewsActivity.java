package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>{

    private static final int NEWS_LOADER_ID = 1;
    private String GUARDIAN_REQUEST_URL_BASE =
            "http://content.guardianapis.com/search?q=london%20AND%20tfl&api-key=test&order-by=newest";
    private NewsAdapter newsAdapter;
    @BindView(R.id.list_view) ListView newsListView;
    @BindView(R.id.empty_text_view) TextView emptyTextView;
    @BindView(R.id.progress_bar) View progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        ButterKnife.bind(this);
        newsListView.setEmptyView(emptyTextView);
        progressBar.setVisibility(View.GONE);

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(newsAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = newsAdapter.getItem(position);
                Uri webpage = Uri.parse(currentNews.getWebUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            newsAdapter.clear();
            emptyTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {
        return new NewsLoader(this, GUARDIAN_REQUEST_URL_BASE);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        progressBar.setVisibility(View.GONE);
        emptyTextView.setText(R.string.no_news_found);
        newsAdapter.clear();
        if ( news!= null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }
}
