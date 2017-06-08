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
import com.schmidthappens.markd.data_objects.TempPanelData;
import com.schmidthappens.markd.menu_option_activities.ElectricalActivity;
import com.schmidthappens.markd.menu_option_activities.ViewPanelActivity;

import java.util.List;

/**
 * Created by Josh on 3/24/2017.
 */

public class PanelListAdapter extends ArrayAdapter<Panel> {
    private float dX, historicX, newX;
    private float originalX = Float.NaN;
    private final float DELTA = (float)-50.0;
    private final int DURATION = 400;

    private ElectricalActivity activityContext = null;
    private static final String TAG = "PanelListAdapter";

    public PanelListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        if(context instanceof ElectricalActivity) {
            activityContext = (ElectricalActivity)context;
        } else {
            Log.e(TAG, "Activity Context not Electrical Activity");
        }
    }

    public PanelListAdapter(Context context, int resource, List<Panel> items) {
        super(context, resource, items);
        if(context instanceof ElectricalActivity) {
            activityContext = (ElectricalActivity)context;
        } else {
            Log.e(TAG, "Activity Context not Electrical Activity");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View panelListView = convertView;

        if (panelListView == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            panelListView = inflater.inflate(R.layout.panel_list_row, null);
        }
        Panel p = getItem(position);

        if (p != null) {
            insertPanelInfo(panelListView, p);
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
                            //Needs to be moved back to original Position
                            if(rowToMove.getX() != originalX) {
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
                //TODO change to http call to delete Panel
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

    //Mark:- Helper Functions
    private void insertPanelInfo(View view, Panel panel) {
        TextView panelDescriptionTextView = (TextView)view.findViewById(R.id.panel_description);
        TextView panelAmperageTextView = (TextView)view.findViewById(R.id.panel_amperage);
        TextView panelInstallDateTextView = (TextView)view.findViewById(R.id.panel_install_date);

        if (panelDescriptionTextView != null) {
            panelDescriptionTextView.setText(panel.getPanelDescription());
        }

        if (panelAmperageTextView != null) {
            panelAmperageTextView.setText(panel.getAmperage().toString());
        }

        if (panelInstallDateTextView != null) {
            panelInstallDateTextView.setText(panel.getInstallDate());
        }
    }

    private void viewClickedPanel(int panelClicked) {
        Class destinationClass = ViewPanelActivity.class;
        //TODO remove when http call comes pass data instead
        TempPanelData.getInstance().currentPanel = panelClicked;
        if(activityContext != null) {
            Intent intentToStartViewPanelActivity = new Intent(activityContext, destinationClass);
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
