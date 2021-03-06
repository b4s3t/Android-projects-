package com.example.android.mynewsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Mohamed on 7/4/2017.
 */

public class NewsUtil {
    public static final String LOG_TAG = NewsUtil.class.getSimpleName();

    private NewsUtil() {
    }

    public static ArrayList<Story> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makehttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in making http request", e);
        }
        ArrayList<Story> result = extractNews(jsonResponse);
        return result;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error in Creating URL", e);
        }
        return url;
    }

    private static String makehttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error in connection!! Bad Response ");
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    private static ArrayList<Story> extractNews(String NewsJSON) {
        if (TextUtils.isEmpty(NewsJSON)) {
            return null;
        }
        ArrayList<Story> news = new ArrayList<Story>();
        try {
            JSONObject baseJsonResponse = new JSONObject(NewsJSON);
            if (!baseJsonResponse.isNull("response")) {
            JSONObject res = baseJsonResponse.getJSONObject("response");
                JSONArray newsArray = res.getJSONArray("results");
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject currentStory = newsArray.getJSONObject(i);
                    String title = currentStory.getString("webTitle");
                    String SectionName = currentStory.getString("sectionName");
                    String webUrl = currentStory.getString("webUrl");
                    String date = currentStory.getString("webPublicationDate");

                    Story story = new Story(SectionName,title,date,webUrl);
                    news.add(story);
                }


            } else {
                news = null;
                Log.e(LOG_TAG, "null news list xD xD");

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error in fetching data", e);
        }
        return news;


    }

}
