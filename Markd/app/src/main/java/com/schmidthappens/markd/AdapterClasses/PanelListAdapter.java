package com.schmidthappens.markd.AdapterClasses;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_subactivities.ViewPanelActivity;

import java.util.List;

/**
 * Created by Josh on 3/24/2017
 */

//TODO abstract duplicate code in PaintListAdapter
// (May need to make LinearLayout and TableRow the same)
public class PanelListAdapter extends ArrayAdapter<Panel> {
    private static final String TAG = "PanelListAdapter";

    private ElectricalActivity activityContext = null;
    private boolean isContractor;
    private String customerId;

    private float dX, historicX, newX;
    private float originalX = Float.NaN;
    private final float DELTA = (float)-50.0;
    private final float LAG = (float)15.0;
    private final int DURATION = 400;

    public PanelListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        if(context instanceof ElectricalActivity) {
            activityContext = (ElectricalActivity)context;
        } else {
            Log.e(TAG, "Activity Context not Electrical Activity");
        }
    }

    public PanelListAdapter(Context context, int resource, List<Panel> items, boolean isContractor, String customerId) {
        super(context, resource, items);
        if(context instanceof ElectricalActivity) {
            activityContext = (ElectricalActivity)context;
        } else {
            Log.e(TAG, "Activity Context not Electrical Activity");
        }
        this.isContractor = isContractor;
        this.customerId = customerId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View panelListView = convertView;

        if (panelListView == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            panelListView = inflater.inflate(R.layout.list_row_panel, null);
        }
        Panel panel = getItem(position);

        if (panel != null) {
            insertPanelInfo(panelListView, panel);
        }

        panelListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TableRow rowToMove = (TableRow)v.findViewById(R.id.panel_list_row_information);
                ImageButton deleteButton = (ImageButton)v.findViewById(R.id.panel_list_row_delete_button);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initializeXValues(rowToMove, event);
                        return true;

                    case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL:
                        //Swiped far enough to left
                        if(newX-historicX < DELTA) {
                            Log.i(TAG, "Swipe Initiated " + position);
                            snapRowToSlideOutPosition(rowToMove, v.getHeight()+30);
                            deleteButton.setClickable(true);
                            return true;
                        } else {
                            Log.d(TAG, "Diff: " + Math.abs(rowToMove.getX()-originalX));
                            //Needs to be moved back to original Position
                            if(Math.abs(rowToMove.getX()-originalX) > LAG) {
                                Log.i(TAG, "Shift back " + position);
                                snapRowToOriginalPosition(rowToMove);
                                deleteButton.setClickable(false);
                            }
                            //Clicked
                            else {
                                Log.i(TAG, "Click Panel " + position);
                                viewClickedPanel(position);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        moveRowWithDrag(rowToMove, event);
                        return true;

                    default:
                        return false;
                }
                return false;
            }
        });

        ImageButton panelDelete = (ImageButton)panelListView.findViewById(R.id.panel_list_row_delete_button);
        panelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityContext != null) {
                    Log.i(TAG, "Delete Panel " + position);
                    activityContext.deletePanel(position);
                } else {
                    Log.e(TAG, "Activity Context NULL");
                }
            }
        });
        panelDelete.setClickable(false);

        return panelListView;
    }

    //Mark:- Panel Deletion

    //Mark:- Helper Functions
    private void insertPanelInfo(View view, Panel panel) {
        TextView panelDescriptionTextView = (TextView)view.findViewById(R.id.panel_description);
        TextView panelAmperageTextView = (TextView)view.findViewById(R.id.panel_amperage);
        TextView panelInstallDateTextView = (TextView)view.findViewById(R.id.panel_install_date);

        if (panelDescriptionTextView != null) {
            panelDescriptionTextView.setText(panel.getPanelDescription());
        }

        if (panelAmperageTextView != null) {
            panelAmperageTextView.setText(panel.getAmperage());
        }

        if (panelInstallDateTextView != null) {
            panelInstallDateTextView.setText(panel.getInstallDate());
        }
    }
    private void viewClickedPanel(int panelClicked) {
        Log.i(TAG, "panelClicked");
        Class destinationClass = ViewPanelActivity.class;
        if(activityContext != null) {
            if(panelClicked >= getCount()) {
                Log.e(TAG, "panelClick greater than list size");
                return;
            }
            Intent intentToStartViewPanelActivity = new Intent(activityContext, destinationClass);
            intentToStartViewPanelActivity.putExtra("panelId", panelClicked);
            intentToStartViewPanelActivity.putExtra("isContractor", isContractor);
            intentToStartViewPanelActivity.putExtra("customerId", customerId);
            activityContext.startActivity(intentToStartViewPanelActivity);
        } else {
            Log.e(TAG, "Activity Context NULL");
        }
    }
    private void initializeXValues(TableRow row, MotionEvent event){
        historicX = event.getRawX();
        dX = row.getX() - event.getRawX();
        if(Float.isNaN(originalX)) {
            Log.i(TAG, "Changed OriginalX");
            originalX = historicX + dX;
        }
    }
    private void snapRowToSlideOutPosition(TableRow row, float amountToLeft) {
        row.animate().x(originalX-amountToLeft).setDuration(DURATION).start();
    }
    private void snapRowToOriginalPosition(TableRow row) {
        row.animate().x(originalX).setDuration(DURATION).start();
    }
    private void moveRowWithDrag(TableRow row, MotionEvent event) {
        row.animate().x(event.getRawX() + dX).setDuration(0).start();
        newX = event.getRawX();
    }
}
