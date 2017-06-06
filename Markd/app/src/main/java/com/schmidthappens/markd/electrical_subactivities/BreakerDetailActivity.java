package com.schmidthappens.markd.electrical_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.schmidthappens.markd.menu_option_activities.ViewPanelActivity;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.BreakerAmperage;
import com.schmidthappens.markd.data_objects.BreakerType;
import com.schmidthappens.markd.data_objects.TempPanelData;

/**
 * Created by Josh on 3/8/2017.
 */

public class BreakerDetailActivity extends AppCompatActivity {
    //XML objects
    private EditText breakerDetailEdit;
    private TextView breakerDetail;
    private Spinner amperageSpinner;
    private TextView amperageText;
    private Spinner breakerTypeSpinner;
    private TextView breakerTypeText;
    private Button deleteBreakerButton;
    private Button editBreakerButton;


    private String breakerNumberString;
    private String breakerDescription;
    private String breakerType;
    private boolean isDoublePoleBottom;
    private String breakerAmperage;
    private boolean isAddBreaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breaker_detail);

        //Set XML Objects
        breakerDetail = (TextView)findViewById(R.id.breaker_description);
        breakerDetailEdit = (EditText)findViewById(R.id.breaker_description_edit);
        amperageSpinner = (Spinner) findViewById(R.id.amperage_spinner);
        amperageText = (TextView)findViewById(R.id.amperage_text);
        deleteBreakerButton = (Button)findViewById(R.id.delete_breaker);
        editBreakerButton = (Button)findViewById(R.id.edit_breaker);
        breakerTypeSpinner = (Spinner)findViewById(R.id.breaker_type_spinner);
        breakerTypeText = (TextView)findViewById(R.id.breaker_type_text);
        //Used to dismiss keyboard on enter pressed
        breakerDetailEdit.setOnEditorActionListener(editOnAction);

        //Initialze Spinner Values
        String[] amperages = BreakerAmperage.getValues();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, amperages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amperageSpinner.setClickable(false);
        amperageSpinner.setAdapter(adapter);
        //Used to dismiss keyboard on touch
        amperageSpinner.setOnTouchListener(spinnerOnTouch);

        String[] breakerTypes = BreakerType.getValues();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, breakerTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breakerTypeSpinner.setClickable(false);
        breakerTypeSpinner.setAdapter(arrayAdapter);
        //Used to dismiss keyboard on touch
        breakerTypeSpinner.setOnTouchListener(spinnerOnTouch);

        //Set Button Listeners
        this.setDeleteBreakerListener(deleteBreakerButton);
        this.setEditBreakerListener(editBreakerButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity != null) {
            String source = "";
            if(intentThatStartedThisActivity.hasExtra("source")) {
                source = intentThatStartedThisActivity.getStringExtra("source");
            }

            if (intentThatStartedThisActivity.hasExtra("breakerNumber")) {
                breakerNumberString = String.valueOf(intentThatStartedThisActivity.getIntExtra("breakerNumber", -1));
                setTitle("Breaker " + breakerNumberString);
            }

            if (intentThatStartedThisActivity.hasExtra("breakerDescription")) {
                breakerDescription = intentThatStartedThisActivity.getStringExtra("breakerDescription");

            }

            if (intentThatStartedThisActivity.hasExtra("breakerAmperage")) {
                breakerAmperage = intentThatStartedThisActivity.getStringExtra("breakerAmperage");
            }

            if (intentThatStartedThisActivity.hasExtra("breakerType")) {
                breakerType = intentThatStartedThisActivity.getStringExtra("breakerType");
                //Breaker is Bottom of Double Pole
                if (breakerType.equals(BreakerType.DoublePoleBottom.toString())) {
                    breakerType = BreakerType.DoublePole.toString();
                    isDoublePoleBottom = true;
                }
            }

            if(source.equals("MainActivity.viewBreaker")) {
                isAddBreaker = false;

                if (breakerDescription.equals("")) {
                    breakerDescription = "No Breaker";
                }

                updateViewingMode();
                updateEditingView();
            }

            else if(source.equals("MainActivity.addBreaker")) {
                isAddBreaker = true;
                updateEditingView();
                changeEditMode();
                deleteBreakerButton.setVisibility(View.GONE);
            }
        }
    }

    // Mark:- Button Listeners
    private void setDeleteBreakerListener(final Button deleteButton) {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String buttonText = deleteButton.getText().toString();
                if(buttonText.equals("Delete")) {
                    deleteBreaker();
                } else if(buttonText.equals("Cancel")) {
                    hideKeyboard();

                    //Change to View Mode
                    changeViewMode();
                    updateEditingView();
                }
            }
        });
    }

    private void setEditBreakerListener(final Button editButton) {
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String buttonText = editButton.getText().toString();
                if(buttonText.equals("Edit")) {
                    //Change to Edit Mode
                    changeEditMode();
                } else if(buttonText.equals("Save")) {
                    //TODO: Change to http call to Server in Future
                    TempPanelData panel = TempPanelData.getInstance();
                    if(isAddBreaker) {
                        //TODO call add breaker
                        panel.addBreaker(makeBreaker());
                        //TODO go back to MainActivity
                        goBackToViewPanel();
                    } else {
                        hideKeyboard();
                        //Collect Updated Info
                        Breaker updatedBreaker = makeBreaker();

                        //TODO: Change to http call to Server in Future
                        panel.updateBreaker(Integer.parseInt(breakerNumberString), updatedBreaker);

                        //Change to View Mode
                        changeViewMode();
                        updateViewingMode();
                    }
                }
            }
        });
    }

    //Mark :- Change Modes
    private void changeViewMode() {
        amperageText.setVisibility(View.VISIBLE);
        amperageSpinner.setVisibility(View.GONE);

        breakerTypeText.setVisibility(View.VISIBLE);
        breakerTypeSpinner.setVisibility(View.GONE);

        breakerDetail.setVisibility(View.VISIBLE);
        breakerDetailEdit.setVisibility(View.GONE);

        deleteBreakerButton.setText("Delete");
        editBreakerButton.setText("Edit");
    }

    private void changeEditMode() {
        amperageText.setVisibility(View.GONE);
        amperageSpinner.setVisibility(View.VISIBLE);

        breakerTypeText.setVisibility(View.GONE);
        breakerTypeSpinner.setVisibility(View.VISIBLE);

        breakerDetail.setVisibility(View.GONE);
        breakerDetailEdit.setVisibility(View.VISIBLE);

        deleteBreakerButton.setText("Cancel");
        editBreakerButton.setText("Save");
    }

    //Mark: :- Helper functions
    private Breaker makeBreaker() {

        //Get BreakerType Value
        String breakerTypeString = (String)breakerTypeSpinner.getItemAtPosition(breakerTypeSpinner.getSelectedItemPosition());
        // Breaker was DoublePole Bottom and set to stay as double-pole
        if(isDoublePoleBottom && breakerTypeString.equals(BreakerType.DoublePole.toString())) {
            breakerTypeString = BreakerType.DoublePoleBottom.toString();
            breakerType = BreakerType.DoublePole.toString();
        } else {
            isDoublePoleBottom = false;
            breakerType = breakerTypeString;
        }
        //Get Breaker Amperage Value
        breakerAmperage = (String)amperageSpinner.getItemAtPosition(amperageSpinner.getSelectedItemPosition());

        //Get Breaker Description Value
        breakerDescription = breakerDetailEdit.getText().toString();

        return new Breaker(Integer.parseInt(breakerNumberString), breakerDescription, BreakerAmperage.fromString(breakerAmperage), BreakerType.fromString(breakerTypeString));
    }

    private void deleteBreaker() {
        Context context = BreakerDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartViewPanelActivity = new Intent(context, destinationClass);
        intentToStartViewPanelActivity.putExtra("actionType", "Delete Breaker");
        intentToStartViewPanelActivity.putExtra("breakerNumber", breakerNumberString);
        startActivity(intentToStartViewPanelActivity);
    }

    private void updateViewingMode() {
        //Set TextView to New Values
        amperageText.setText(breakerAmperage);
        breakerTypeText.setText(breakerType);
        breakerDetail.setText(breakerDescription);
    }

    private void updateEditingView() {
        amperageSpinner.setSelection(BreakerAmperage.fromString(breakerAmperage).ordinal());
        breakerTypeSpinner.setSelection(BreakerType.fromString(breakerType).ordinal());
        breakerDetailEdit.setText(breakerDescription);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void goBackToViewPanel() {
        Context context = BreakerDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        startActivity(intentToStartMainActivity);
    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                hideKeyboard();
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener editOnAction = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(breakerDetailEdit.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
            return false;
        }
    };
}
