package com.schmidthappens.markd.RecyclerViewClasses;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.schmidthappens.markd.R;

/**
 * Created by Josh on 3/6/2017.
 */

public class BreakerViewHolder extends RecyclerView.ViewHolder {
    protected TextView breaker_description_textview;
    protected TextView breaker_number_textview;

    public BreakerViewHolder(View v){
        super(v);
        breaker_description_textview = (TextView) v.findViewById(R.id.breaker_item_description);
        breaker_number_textview = (TextView) v.findViewById(R.id.breaker_item_number);
    }
}
