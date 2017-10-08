package com.example.android.mynewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohamed on 7/4/2017.
 */

public class NewsAdapter extends ArrayAdapter<Story> {

    /**
     * Constructs a new {@link NewsAdapter}.
     * * @param context     of the app
     *
     * @param news is the list of earthquakes, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<Story> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about the Book at the given position
     * in the list of earthquakes.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_custome_list, parent, false);
        }
        Story CurrentStory = getItem(position);
        TextView sectionTxt = (TextView) listItemView.findViewById(R.id.section_id);
        TextView titleTxt = (TextView) listItemView.findViewById(R.id.title_id);
        TextView dateTxt = (TextView) listItemView.findViewById(R.id.Date_id);

        sectionTxt.setText(CurrentStory.getmSectionName());
        titleTxt.setText(CurrentStory.getStoryTitle());
        dateTxt.setText(CurrentStory.getDate());

        return listItemView;
    }
}
