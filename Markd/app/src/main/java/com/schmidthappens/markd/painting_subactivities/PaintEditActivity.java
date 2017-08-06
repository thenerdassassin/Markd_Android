package com.schmidthappens.markd.painting_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.PaintObject;
import com.schmidthappens.markd.data_objects.TempPaintData;
import com.schmidthappens.markd.menu_option_activities.PaintingActivity;

/**
 * Created by Josh on 7/22/2017.
 */

public class PaintEditActivity extends AppCompatActivity {
    //XML Objects
    EditText editLocation;
    EditText editBrand;
    EditText editColor;
    Button saveButton;

    int paintId;
    boolean isExterior;

    private static final String TAG = "PaintEditActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_paint);

        SessionManager sessionManager = new SessionManager(PaintEditActivity.this);
        sessionManager.checkLogin();

        Intent intent = getIntent();

        //TODO instantiate XML Objects
        if(intent != null) {
            instantiateEditTextObjects();

            paintId = intent.getIntExtra("id", -1);
            Log.d("TAG", ""+paintId);
            isExterior = intent.hasExtra("isExterior");


            if(intent.hasExtra("location")) {
                editLocation.setText(intent.getStringExtra("location"));
            }

            if(intent.hasExtra("brand")) {
                editBrand.setText(intent.getStringExtra("brand"));
            }

            if(intent.hasExtra("color")) {
                editColor.setText(intent.getStringExtra("color"));
            }
        } else {
            Log.e(TAG, "Intent is Null");
            goBackToPaintingActivity();
        }

        saveButton = (Button)findViewById(R.id.paint_edit_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(PaintEditActivity.this.getCurrentFocus());
                savePaintObject();
                goBackToPaintingActivity();
            }
        });
    }

    public void instantiateEditTextObjects(){
        editLocation = (EditText)findViewById(R.id.paint_edit_location);
        setEnterButtonToKeyboardDismissal(editLocation);

        editBrand = (EditText)findViewById(R.id.paint_edit_brand);
        setEnterButtonToKeyboardDismissal(editBrand);

        editColor = (EditText)findViewById(R.id.paint_edit_color);
        setEnterButtonToKeyboardDismissal(editColor);
    }

    private void savePaintObject() {
        String location = editLocation.getText().toString();
        String brand = editBrand.getText().toString();
        String color = editColor.getText().toString();

        if(paintId == -1) {
            //New Paint
            PaintObject newPaint = new PaintObject(location, brand, color);
            //TODO change to http call to add new PaintObject
            if(isExterior) {
                TempPaintData.getInstance().putExteriorPaint(newPaint);
            } else {
                TempPaintData.getInstance().putInteriorPaint(newPaint);
            }

        } else {
            //TODO change to http call to update PaintObject
            TempPaintData.getInstance().updatePaint(paintId, location, brand, color, isExterior);
        }
    }

    private void goBackToPaintingActivity(){
        Intent paintingActivityIntent = new Intent(getApplicationContext(), PaintingActivity.class);
        startActivity(paintingActivityIntent);
        finish();
    }

    //Mark:- Helper functions
    //Makes the enter button dismiss soft keyboard
    private void setEnterButtonToKeyboardDismissal(final EditText view) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });
    }

    //Hides Keyboard
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
