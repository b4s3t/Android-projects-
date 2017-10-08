package com.example.android.mynewsapp;

/**
 * Created by Mohamed on 7/4/2017.
 */

public class Story {
    public String mSectionName;
    public String mTitle;
    public String mDate;
    public String mWebUrl;

    Story(String sectionName,String title, String date ,String webUrl) {
        mSectionName = sectionName;
        mDate = date;
        mTitle = title;
        mWebUrl = webUrl;

    }

    public String getmSectionName() {
        return "Section: ".toUpperCase() + mSectionName;
    }

    public String getStoryTitle() {
        return "Title: ".toUpperCase() + mTitle;
    }

    public String getDate() {

        return "Publish date: ".toUpperCase()+ mDate;
    }

    public String getmWebUrl() {

        return mWebUrl;
    }


}
