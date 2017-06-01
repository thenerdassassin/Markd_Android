package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.LandscapingSeason;

import java.util.List;

/**
 * Created by Josh on 6/1/2017.
 */

public class LandscapingHistoryAdapter extends ArrayAdapter<LandscapingSeason> {
    public LandscapingHistoryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public LandscapingHistoryAdapter(Context context, int resource, List<LandscapingSeason> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View landscapingHistoryRow = convertView;

        if (landscapingHistoryRow == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            landscapingHistoryRow = inflater.inflate(R.layout.landscaping_season_history_row, null);
        }

        LandscapingSeason season = getItem(position);

        if (season != null) {
            TextView landscapingSeasonView = (TextView) landscapingHistoryRow.findViewById(R.id.landscaping_season);
            TextView landscapingRatingView = (TextView) landscapingHistoryRow.findViewById(R.id.landscaping_rating);
            TextView landscapingPoundPerAcreView = (TextView) landscapingHistoryRow.findViewById(R.id.landscaping_pound_per_acre);
            TextView landscapingCommentsView = (TextView) landscapingHistoryRow.findViewById(R.id.landscaping_comments);

            if (landscapingSeasonView != null) {
                landscapingSeasonView.setText(season.getSeason() + " " + Integer.toString(season.getYear()));
            }

            if (landscapingRatingView != null) {
                landscapingRatingView.setText(Integer.toString(season.getRating()) + "/5");
            }

            if (landscapingPoundPerAcreView != null) {
                landscapingPoundPerAcreView.setText(Double.toString(season.getPoundsPerAcre())+"lb/acre");
            }

            if (landscapingCommentsView != null) {
                landscapingCommentsView.setText(season.getComments());
            }
        }

        return landscapingHistoryRow;
    }
}
