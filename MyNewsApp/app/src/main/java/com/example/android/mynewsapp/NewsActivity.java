package com.example.android.mynewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.mynewsapp.NewsUtil.LOG_TAG;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<Story>> {

    private static final int EARTHQUAKE_LOADER_ID = 1;
    private String Query_URL = "http://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2015-01-01&api-key=test&page-size=20&show-tags=contributor";
    private TextView empty;
    public NewsAdapter AsyncAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        AsyncAdapter = new NewsAdapter(this, new ArrayList<Story>());
        empty = (TextView) findViewById(R.id.empty_list_item);

        if (isNetworkAvailable()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, NewsActivity.this);
            ListView newsListView = (ListView) findViewById(R.id.list_id);
            newsListView.setAdapter(AsyncAdapter);

            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Story currentNews = AsyncAdapter.getItem(position);

                    Uri newsUri = Uri.parse(currentNews.getmWebUrl());

                    Log.d(LOG_TAG, newsUri.toString() + "checked");

                    if (currentNews.getmWebUrl() == null || TextUtils.isEmpty(currentNews.getmWebUrl())) {
                        Toast.makeText(NewsActivity.this, NewsActivity.this.getResources().getString(R.string.no_link_found), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                        startActivity(websiteIntent);
                    }
                }
            });

        } else {
            empty.setText(R.string.network_not_found);
            emptyState();

            Toast.makeText(getApplicationContext(),
                    R.string.network_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, Query_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        if (data != null && !data.isEmpty()) {
            AsyncAdapter.clear();
            AsyncAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        AsyncAdapter.clear();


    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void emptyState() {

        ListView view = (ListView) findViewById(R.id.list_id);
        view.setEmptyView(findViewById(R.id.empty_list_item));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
                android.R.id.text1);
        view.setAdapter(adapter1);
    }

}
