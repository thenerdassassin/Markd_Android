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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.PaintObject;
import com.schmidthappens.markd.data_objects.TempPanelData;
import com.schmidthappens.markd.menu_option_activities.PaintingActivity;
import com.schmidthappens.markd.menu_option_activities.ViewPanelActivity;

import java.util.List;

/**
 * Created by Josh on 5/29/2017.
 */

//TODO abstract duplicate code in PanelListAdapter
// (May need to make LinearLayout and TableRow the same)
public class PaintListAdapter extends ArrayAdapter<PaintObject> {

    private float dX, historicX, newX;
    private float originalX = Float.NaN;
    private final float DELTA = (float)-50.0;
    private final float LAG = (float)15.0;
    private final int DURATION = 400;
    //TODO is there a better way to do this
    private boolean isExterior = false;

    private PaintingActivity activityContext = null;
    private static final String TAG = "PaintListAdapter";

    public PaintListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        if(context instanceof PaintingActivity) {
            activityContext = (PaintingActivity)context;
        } else {
            Log.e(TAG, "Activity Context not PaintingActivity");
        }
    }

    public PaintListAdapter(Context context, int resource, List<PaintObject> items) {
        super(context, resource, items);
        if(context instanceof PaintingActivity) {
            activityContext = (PaintingActivity)context;
        } else {
            Log.e(TAG, "Activity Context not PaintingActivity");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View paintListView = convertView;

        if (paintListView == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            paintListView = inflater.inflate(R.layout.paint_list_row, null);
        }

        PaintObject paintObject = getItem(position);

        if (paintObject != null) {
            insertPaintInfo(paintListView, paintObject);
        }

        paintListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LinearLayout rowToMove = (LinearLayout)v.findViewById(R.id.paint_list_row_information);
                ImageButton deleteButton = (ImageButton)v.findViewById(R.id.paint_list_row_delete_button);

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
                                //viewClickedPanel(position);
                                Toast.makeText(activityContext, "Edit " + getItem(position).getLocation(), Toast.LENGTH_SHORT).show();
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

        ImageButton paintObjectDeleteButton = (ImageButton)paintListView.findViewById(R.id.paint_list_row_delete_button);
        paintObjectDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO change to http call to delete PaintObject
                if(activityContext != null) {
                    Log.i(TAG, "Delete Paint Item " + position);
                    activityContext.deletePaintObject(position, isExterior);
                } else {
                    Log.e(TAG, "Activity Context NULL");
                }
            }
        });
        paintObjectDeleteButton.setClickable(false);

        return paintListView;
    }

    //Mark:- Helper Functions
    public void setIsExterior(boolean isExterior) {
        this.isExterior = isExterior;
    }

    private void insertPaintInfo(View view, PaintObject paintObject) {
        TextView paintLocationView = (TextView) view.findViewById(R.id.paint_location);
        TextView paintManufacturerView = (TextView) view.findViewById(R.id.paint_manufacturer);
        TextView paintColorView = (TextView) view.findViewById(R.id.paint_color);

        if (paintLocationView != null) {
            paintLocationView.setText(paintObject.getLocation());
        }

        if (paintManufacturerView != null) {
            paintManufacturerView.setText(paintObject.getManufacturer());
        }

        if (paintColorView != null) {
            paintColorView.setText(paintObject.getColor());
        }
    }

    //TODO change to open clicked paint object
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

    private void initializeXValues(LinearLayout row, MotionEvent event){
        historicX = event.getRawX();
        dX = row.getX() - event.getRawX();
        if(Float.isNaN(originalX)) {
            Log.i(TAG, "Changed OriginalX");
            originalX = historicX + dX;
        }
    }

    private void snapRowToSlideOutPosition(LinearLayout row, float amountToLeft) {
        row.animate().x(originalX-amountToLeft).setDuration(DURATION).start();
    }

    private void snapRowToOriginalPosition(LinearLayout row) {
        row.animate().x(originalX).setDuration(DURATION).start();
    }

    private void moveRowWithDrag(LinearLayout row, MotionEvent event) {
        row.animate().x(event.getRawX() + dX).setDuration(0).start();
        newX = event.getRawX();
    }
}
