package com.example.android.mynewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed on 7/4/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<Story>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    public String incomingUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context , String url) {
        super(context);
        incomingUrl= url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<Story> loadInBackground() {
        if (incomingUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of News.
        ArrayList<Story> news = NewsUtil.fetchNewsData(incomingUrl);

        return news;
    }

}
