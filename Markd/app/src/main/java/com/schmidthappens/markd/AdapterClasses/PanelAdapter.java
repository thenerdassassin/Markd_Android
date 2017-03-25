package com.schmidthappens.markd.AdapterClasses;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.BreakerType;
import com.schmidthappens.markd.data_objects.Panel;


/**
 * Created by Josh on 3/6/2017.
 * Adapter for the RecyclerView to put in the Breakers
 */

public class PanelAdapter extends RecyclerView.Adapter<PanelAdapter.BreakerViewHolder> {
    private Panel panel;
    private int breakerNumber;

    // On click handler for when a user clicks on a breaker to view
    private final PanelAdapterOnClickHandler breaker_click_handler;

    public PanelAdapter(Panel panel, PanelAdapterOnClickHandler clickHandler) {
        this.panel = panel;
        breaker_click_handler = clickHandler;
    }

    // Mark :- RecyclerView Functions

    //Returns the number of breakers to display
    @Override
    public int getItemCount() {
        return panel.getBreakerList().size();
    }

    // Called by the RecyclerView to display the breaker at the position.
    @Override
    public void onBindViewHolder(BreakerViewHolder breakerViewHolder, int index) {
        Breaker breaker = panel.getBreakerList().get(index);
        breakerViewHolder.breaker_description_textview.setText(breaker.getBreakerDescription());
        breakerViewHolder.breaker_number_textview.setText(String.valueOf(breaker.getNumber()));

        //Check if DoublePole
        if(breaker.getBreakerType().equals(BreakerType.DoublePole)) {
            breakerViewHolder.breaker_bottom_views.setVisibility(View.VISIBLE);
        } else {
            breakerViewHolder.breaker_bottom_views.setVisibility(View.INVISIBLE);
        }

        //Check if bottom of DoublePole
        if(breaker.getBreakerType().equals(BreakerType.DoublePoleBottom)) {
            breakerViewHolder.breaker_top_views.setVisibility(View.VISIBLE);
        } else {
            breakerViewHolder.breaker_top_views.setVisibility(View.INVISIBLE);
        }
    }

    /* This gets called when the RecyclerView is laid out.
     * Enough ViewHolders will be created to fill the screen
     * and allow for scrolling.
    */
    @Override
    public BreakerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.breaker_list_item, viewGroup, false);
        return new BreakerViewHolder(itemView);
    }

    // Mark:- ViewHolder

    public class BreakerViewHolder extends ViewHolder implements View.OnClickListener {
        protected TextView breaker_description_textview;
        protected TextView breaker_number_textview;
        protected LinearLayout breaker_bottom_views;
        protected LinearLayout breaker_top_views;

        public BreakerViewHolder(View v){
            super(v);
            breaker_description_textview = (TextView) v.findViewById(R.id.breaker_item_description);
            breaker_number_textview = (TextView) v.findViewById(R.id.breaker_item_number);
            breaker_bottom_views = (LinearLayout) v.findViewById(R.id.double_pole_bottom);
            breaker_top_views = (LinearLayout) v.findViewById(R.id.double_pole_top);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Breaker breakerClicked = panel.getBreakerList().get(adapterPosition);
            breaker_click_handler.onClick(breakerClicked);
        }
    }
    // Mark:- Gesture Functions

   // The Interface that receives onClick messages
    public interface PanelAdapterOnClickHandler {
        void onClick(Breaker breakerClicked);
    }

   // Mark:- Getters/Setters
    public void switchPanel(Panel newPanel) {
        this.panel = newPanel;
        notifyDataSetChanged();
    }

    public int getBreakerNumber() {
        return breakerNumber;
    }

    public void setBreakerNumber(int breakerNumber) {
        this.breakerNumber = breakerNumber;
    }
}
