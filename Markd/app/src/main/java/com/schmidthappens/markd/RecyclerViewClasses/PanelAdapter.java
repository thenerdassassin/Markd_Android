package com.schmidthappens.markd.RecyclerViewClasses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Josh on 3/6/2017.
 * Adapter for the RecyclerView to put in the Breakers
 */

public class PanelAdapter extends RecyclerView.Adapter<BreakerViewHolder> {

    private Panel panel;

    public PanelAdapter() {
        List<Breaker> breakerList = new LinkedList<Breaker>();
        breakerList.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList.add(new Breaker(7, "Washing Machine"));
        breakerList.add(new Breaker(8, "Dryer"));
        breakerList.add(new Breaker(9, "Hot water Heater"));
        breakerList.add(new Breaker(10, "Well pump"));
        breakerList.add(new Breaker(11, "Refrigerator"));
        breakerList.add(new Breaker(12, "Microwave"));
        breakerList.add(new Breaker(13, "Oven"));
        breakerList.add(new Breaker(14, "Kitchen Receptacles"));
        breakerList.add(new Breaker(15, "Kitchen Island Receptacles"));
        breakerList.add(new Breaker(16, "Kitchen Lighting"));
        breakerList.add(new Breaker(17, "Spot Lights"));
        breakerList.add(new Breaker(18, "Garbage Disposal"));
        breakerList.add(new Breaker(19, "Dishwasher"));
        breakerList.add(new Breaker(20, "Kitchen Hood"));
        breakerList.add(new Breaker(21, "Dining Room Receptacles"));
        breakerList.add(new Breaker(22, "Dining Room Lighting"));
        breakerList.add(new Breaker(23, "Living Room Receptacles"));
        breakerList.add(new Breaker(24, "Family Room Lighting"));
        breakerList.add(new Breaker(25, "Foyer Receptacles"));
        breakerList.add(new Breaker(26, "Foyer Lighting"));
        breakerList.add(new Breaker(27, "Furnace"));
        breakerList.add(new Breaker(28, "Air Compressor"));
        breakerList.add(new Breaker(29, "Air Handler"));
        breakerList.add(new Breaker(30, "Central Vacuum"));
        breakerList.add(new Breaker(31, "Sump Pump"));
        breakerList.add(new Breaker(32, "Basement Lighting"));
        breakerList.add(new Breaker(33, "Exterior Lighting"));
        breakerList.add(new Breaker(34, "Landscape Lighting"));
        breakerList.add(new Breaker(35, "Garage Door Receptacles"));

        this.panel = new Panel(breakerList);
    }

    @Override
    public int getItemCount() {
        return panel.getBreakerList().size();
    }

    @Override
    public void onBindViewHolder(BreakerViewHolder breakerViewHolder, int index) {
        Breaker breaker = panel.getBreakerList().get(index);
        breakerViewHolder.breaker_description_textview.setText(breaker.getBreaker_description());
        breakerViewHolder.breaker_number_textview.setText(String.valueOf(breaker.getNumber()));
    }

    @Override
    public BreakerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.breaker_list_item, viewGroup, false);

        return new BreakerViewHolder(itemView);
    }
}
