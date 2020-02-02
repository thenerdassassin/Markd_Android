package com.markd.applications.android.home.customer_subactivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.customer_menu_activities.PaintingActivity;
import com.markd.applications.android.home.data_objects.PaintSurface;
import com.markd.applications.android.home.data_objects.TempCustomerData;
import com.markd.applications.android.home.utilities.StringUtilities;

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
    Button deleteButton;

    int paintId;
    boolean isExterior;

    private static final String TAG = "PaintEditActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private AlertDialog alertDialog;
    private String customerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_paint);

        authentication = new FirebaseAuthentication(this);

        setTitle("Edit Paint Surface");
        Intent intent = getIntent();

        if(intent != null) {
            instantiateXMLObjects();
            processIntentExtras(intent);
            customerData = new TempCustomerData(customerId, null);
            setInstallDateButton.setOnClickListener(setPaintDateButtonClickListener);
            saveButton.setOnClickListener(onSaveClickListener);
            deleteButton.setOnClickListener(onDeleteClickListener);
        } else {
            Log.e(TAG, "Intent or customerId is Null");
            goBackToPaintingActivity();
        }
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
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void instantiateXMLObjects(){
        editLocation = (EditText)findViewById(R.id.paint_edit_location);
        setEnterButtonToKeyboardDismissal(editLocation);

        editInstallDate = (TextView)findViewById(R.id.paint_edit_install_date);
        setInstallDateButton = (Button)findViewById(R.id.paint_set_install_date);

        editBrand = (EditText)findViewById(R.id.paint_edit_brand);
        setEnterButtonToKeyboardDismissal(editBrand);

        editColor = (EditText)findViewById(R.id.paint_edit_color);
        setEnterButtonToKeyboardDismissal(editColor);

        saveButton = (Button)findViewById(R.id.paint_edit_save_button);
        deleteButton = (Button)findViewById(R.id.paint_edit_delete_button);
    }
    private void processIntentExtras(Intent intent){
        paintId = intent.getIntExtra("id", -1);
        Log.d("TAG", "paintId:"+paintId);
        isExterior = intent.getBooleanExtra("isExterior", false);

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
        if(intent.hasExtra("customerId")) {
            customerId = intent.getStringExtra("customerId");
        } else {
            Log.e(TAG, "No customer id in intent");
            goBackToPaintingActivity();
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
    private View.OnClickListener onDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(PaintEditActivity.this.getCurrentFocus());
            showDeletePaintWarning();
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
    private void showDeletePaintWarning() {
        alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Paint Surface")
                .setMessage("This action can not be reversed. Are you sure you want to delete this paint surface?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                        Log.d(TAG, "Cancel the paint deletion");
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Delete button
                        Log.d(TAG, "Delete the paint surface");
                        deletePaint();
                        dialog.dismiss();
                        goBackToPaintingActivity();
                    }
                })
                .create();
        alertDialog.show();
    }
    private void deletePaint() {
        if(isExterior) {
            customerData.removeExteriorPaintSurface(paintId);
        } else {
            customerData.removeInteriorPaintSurface(paintId);
        }
    }
    private void goBackToPaintingActivity(){
        Intent paintingActivityIntent = new Intent(this, PaintingActivity.class);
        if(customerId != null && !customerId.equals(authentication.getCurrentUser().getUid())) {
            paintingActivityIntent.putExtra("isContractor", true);
            paintingActivityIntent.putExtra("customerId", customerId);
        }
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
