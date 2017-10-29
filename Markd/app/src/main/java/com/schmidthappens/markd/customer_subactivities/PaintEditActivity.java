package com.schmidthappens.markd.customer_subactivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.PaintSurface;
import com.schmidthappens.markd.customer_menu_activities.PaintingActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.Calendar;

/**
 * Created by Josh on 7/22/2017.
 */

public class PaintEditActivity extends AppCompatActivity {
    //XML Objects
    EditText editLocation;
    TextView editInstallDate;
    Button setInstallDateButton;
    EditText editBrand;
    EditText editColor;
    Button saveButton;

    int paintId;
    boolean isExterior;

    private static final String TAG = "PaintEditActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_paint);

        authentication = new FirebaseAuthentication(this);
        customerData = new TempCustomerData(authentication, null); //TODO: add listener

        setTitle("Edit Paint Surface");
        Intent intent = getIntent();

        if(intent != null) {
            instantiateEditTextObjects();
            processIntentExtras(intent);
        } else {
            Log.e(TAG, "Intent is Null");
            goBackToPaintingActivity();
        }

        setInstallDateButton.setOnClickListener(setPaintDateButtonClickListener);
        saveButton.setOnClickListener(onSaveClickListener);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    private void instantiateEditTextObjects(){
        editLocation = (EditText)findViewById(R.id.paint_edit_location);
        setEnterButtonToKeyboardDismissal(editLocation);

        editInstallDate = (TextView)findViewById(R.id.paint_edit_install_date);
        setInstallDateButton = (Button)findViewById(R.id.paint_set_install_date);

        editBrand = (EditText)findViewById(R.id.paint_edit_brand);
        setEnterButtonToKeyboardDismissal(editBrand);

        editColor = (EditText)findViewById(R.id.paint_edit_color);
        setEnterButtonToKeyboardDismissal(editColor);

        saveButton = (Button)findViewById(R.id.paint_edit_save_button);
    }

    private void processIntentExtras(Intent intent){
        paintId = intent.getIntExtra("id", -1);
        Log.d("TAG", "paintId:"+paintId);
        isExterior = intent.hasExtra("isExterior");

        if(intent.hasExtra("location")) {
            editLocation.setText(intent.getStringExtra("location"));
        }

        if(intent.hasExtra("paintDate")) {
            editInstallDate.setText(intent.getStringExtra("paintDate"));
        } else {
            editInstallDate.setText(StringUtilities.getCurrentDateString());
        }

        if(intent.hasExtra("brand")) {
            editBrand.setText(intent.getStringExtra("brand"));
        }

        if(intent.hasExtra("color")) {
            editColor.setText(intent.getStringExtra("color"));
        }
    }

    //Mark: Listeners
    private View.OnClickListener onSaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(PaintEditActivity.this.getCurrentFocus());
            savePaintSurface();
            goBackToPaintingActivity();
        }
    };

    private View.OnClickListener setPaintDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(PaintEditActivity.this.getCurrentFocus());
            showDatePickerDialog(v);
        }
    };

    private void savePaintSurface() {
        String location = editLocation.getText().toString();
        String brand = editBrand.getText().toString();
        String color = editColor.getText().toString();

        String date = editInstallDate.getText().toString();
        int month = StringUtilities.getMonthFromDotFormattedString(date);
        int day = StringUtilities.getDayFromDotFormmattedString(date);
        int year = StringUtilities.getYearFromDotFormmattedString(date);

        PaintSurface paintSurface = new PaintSurface(location, brand, color, month, day, year);

        if(isExterior) {
            customerData.updateExteriorPaintSurface(paintId, paintSurface);
        } else {
            customerData.updateInteriorPaintSurface(paintId, paintSurface);
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

    //Mark:- DatePickerDialog
    private void changeInstallDate(int month, int day, int year) {
        editInstallDate.setText(StringUtilities.getDateString(month, day, year));
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            pickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return pickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            Calendar current = Calendar.getInstance();

            //Check to make sure date is not in future
            if(selected.getTime().after(current.getTime())) {
                Log.i(TAG, "Selected Invalid Date");
                Toast.makeText(getActivity().getApplicationContext(), "Install date must not be in future.", Toast.LENGTH_SHORT).show();
                return;
            }

            ((PaintEditActivity)getActivity()).changeInstallDate(month+1, dayOfMonth, year);
        }
    }
}
