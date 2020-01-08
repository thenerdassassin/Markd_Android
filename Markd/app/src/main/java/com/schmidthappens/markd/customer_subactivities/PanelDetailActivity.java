package com.schmidthappens.markd.customer_subactivities;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.DateUtitilities;
import com.schmidthappens.markd.utilities.NumberPickerUtilities;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.Calendar;

/**
 * Created by Josh on 3/22/2017.
 */

public class PanelDetailActivity extends AppCompatActivity {
    private static final String TAG = "PanelDetailActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorViewingPage;
    private int panelId;
    private boolean isNewPanel = false;
    private int originalNumberOfBreakers;

    //XML Objects
    EditText panelDescription;
    NumberPicker numberOfBreakers;
    CheckBox isSubPanel;
    TextView panelInstallDate;
    Button setPanelInstallDateButton;
    NumberPicker amperageNumberPicker;
    NumberPicker manufacturerNumberPicker;
    Button savePanelButton;
    Button deletePanelButton;

    AlertDialog alertDialog;
    private final String[] panelManufacturers = Panel.getPanelManufacturers();
    private final String[] mainPanelAmperages = Panel.getMainPanelAmperageValues();
    private final String[] subPanelAmperages = Panel.getSubPanelAmperageValues();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_panel);
        authentication = new FirebaseAuthentication(this);
        initializeXMLObjects();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        processIntent(getIntent());
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

    private void initializeXMLObjects() {
        panelDescription = (EditText)findViewById(R.id.electrical_panel_description);
        numberOfBreakers = (NumberPicker)findViewById(R.id.electrical_panel_number_of_breakers);
        isSubPanel = (CheckBox)findViewById(R.id.is_sub_panel);
        panelInstallDate = (TextView)findViewById(R.id.electrical_panel_install_date);
        setPanelInstallDateButton = (Button)findViewById(R.id.electrical_panel_set_install_date);
        amperageNumberPicker = (NumberPicker) findViewById(R.id.panel_amperage_spinner);
        manufacturerNumberPicker = (NumberPicker) findViewById(R.id.panel_manufacturer_spinner);
        savePanelButton = (Button)findViewById(R.id.electrical_save_panel_button);
        deletePanelButton = (Button)findViewById(R.id.electrical_delete_panel_button);

        setEnterButtonToKeyboardDismissal(panelDescription);
        setUpNumberPickers();
        setClickListeners();
    }
    private void processIntent(Intent intentThatStartedThisActivity) {
        if(intentThatStartedThisActivity != null) {
            isContractorViewingPage = intentThatStartedThisActivity.getBooleanExtra("isContractor", false);
            if(intentThatStartedThisActivity.hasExtra("customerId")) {
                customerData = new TempCustomerData(intentThatStartedThisActivity.getStringExtra("customerId"), null);
            } else {
                Log.e(TAG, "No customer id");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }

            String amperageString = "";

            isNewPanel = intentThatStartedThisActivity.hasExtra("newPanel");
            panelId = intentThatStartedThisActivity.getIntExtra("panelId", -1);

            if(intentThatStartedThisActivity.hasExtra("panelDescription")) {
                String panelDescriptionString = intentThatStartedThisActivity.getStringExtra("panelDescription");
                panelDescription.setText(panelDescriptionString);
                panelDescription.setSelection(panelDescriptionString.length()); //Sets cursor to end of EditText
            }

            numberOfBreakers.setValue(intentThatStartedThisActivity.getIntExtra("numberOfBreakers", 1));
            originalNumberOfBreakers = numberOfBreakers.getValue();

            if(intentThatStartedThisActivity.hasExtra("panelInstallDate")) {
                panelInstallDate.setText(intentThatStartedThisActivity.getStringExtra("panelInstallDate"));
            }

            if(intentThatStartedThisActivity.hasExtra("panelAmperage")) {
                amperageString = intentThatStartedThisActivity.getStringExtra("panelAmperage");
            }

            boolean isMainPanel = intentThatStartedThisActivity.getBooleanExtra("isMainPanel", true);
            if(!isMainPanel) {
                isSubPanel.setChecked(true);
                NumberPickerUtilities.setPicker(amperageNumberPicker, amperageString, subPanelAmperages);
                setAsSubAmperages();
                NumberPickerUtilities.setPicker(amperageNumberPicker, amperageString, subPanelAmperages);
            } else {
                isSubPanel.setChecked(false);
                NumberPickerUtilities.setPicker(amperageNumberPicker, amperageString, mainPanelAmperages);
                setAsMainAmperages();
                NumberPickerUtilities.setPicker(amperageNumberPicker, amperageString, mainPanelAmperages);
            }


            if(intentThatStartedThisActivity.hasExtra("manufacturer")) {
                String manufacturer = intentThatStartedThisActivity.getStringExtra("manufacturer");
                NumberPickerUtilities.setPicker(manufacturerNumberPicker, manufacturer, panelManufacturers);
            }
        }
    }
    private void setUpNumberPickers() {
        numberOfBreakers.setMinValue(1);
        numberOfBreakers.setMaxValue(52);
        numberOfBreakers.setWrapSelectorWheel(false);

        manufacturerNumberPicker.setMinValue(0);
        manufacturerNumberPicker.setMaxValue(panelManufacturers.length-1);
        manufacturerNumberPicker.setDisplayedValues(panelManufacturers);

        amperageNumberPicker.setMinValue(0);
        setAsMainAmperages();
    }
    private void setAsMainAmperages() {
        amperageNumberPicker.setValue(0);
        amperageNumberPicker.setDisplayedValues(mainPanelAmperages);
        amperageNumberPicker.setMaxValue(mainPanelAmperages.length-1);
    }
    private void setAsSubAmperages() {
        amperageNumberPicker.setValue(0);
        amperageNumberPicker.setMaxValue(subPanelAmperages.length-1);
        amperageNumberPicker.setDisplayedValues(subPanelAmperages);
    }
    private void setClickListeners() {
        setPanelInstallDateButton.setOnClickListener(panelInstallDateButtonClickListener);
        savePanelButton.setOnClickListener(onSaveClicked);
        deletePanelButton.setOnClickListener(onDeleteClicked);
        isSubPanel.setOnClickListener(isSubPanelClick);
    }

    // Mark:- Click Listeners
    private View.OnClickListener isSubPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(view);
            if (view instanceof CheckBox) {
                CheckBox isSubPanel = (CheckBox)view;
                if (isSubPanel.isChecked()) {
                    setAsSubAmperages();
                } else {
                    setAsMainAmperages();
                }
            }
        }
    };
    private View.OnClickListener panelInstallDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(PanelDetailActivity.this.getCurrentFocus());
            showDatePickerDialog(v);
        }
    };
    private View.OnClickListener onSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(v);
            Log.i(TAG, "Save Panel");
            savePanel();
        }
    };
    private View.OnClickListener onDeleteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(v);
            Log.i(TAG, "Delete Panel");
            deletePanel(panelId);
        }
    };
    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        if(isNewPanel) {
            backToElectricalActivity();
        }
        backToViewPanelActivity();
        return true;
    }

    //Mark:- Date Picker
    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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

            String newDate = "";
            if(month < 9) {
                newDate += "0";
            }
            newDate += Integer.toString(month+1) + ".";
            if(dayOfMonth < 10) {
                newDate += "0";
            }

            newDate += dayOfMonth + "." + (Integer.toString(year).substring(2));
            ((PanelDetailActivity)getActivity()).changeInstallDate(newDate);
        }
    }

    //Mark:- Helper Functions
    private void changeInstallDate(String newDate) {
        panelInstallDate.setText(newDate);
    }
    private void savePanel() {
        Panel panelToUpdate;

        //Get Info
        String panelDescriptionString = panelDescription.getText().toString();
        if(panelDescriptionString.isEmpty()) {
            panelDescriptionString = "Panel";
        }
        int breakersNumber = numberOfBreakers.getValue();
        boolean isSubPanelChecked = isSubPanel.isChecked();
        String panelInstallDateString = panelInstallDate.getText().toString();
        if(panelInstallDateString.isEmpty()) {
            panelInstallDateString = StringUtilities.getDateString(DateUtitilities.getCurrentMonth()+1, DateUtitilities.getCurrentDay(), DateUtitilities.getCurrentYear());
        }
        String panelAmpString;
        if(isSubPanelChecked) {
            panelAmpString = subPanelAmperages[amperageNumberPicker.getValue()];
        } else {
            panelAmpString = mainPanelAmperages[amperageNumberPicker.getValue()];
        }
        String manufacturerString = panelManufacturers[manufacturerNumberPicker.getValue()];

        if(isNewPanel) {
            panelToUpdate = new Panel();
        } else {
            panelToUpdate = customerData.getPanels().get(panelId);
        }
        panelToUpdate = panelToUpdate.updatePanel(panelDescriptionString, breakersNumber, !isSubPanelChecked, panelInstallDateString, panelAmpString, manufacturerString);
        if(originalNumberOfBreakers <= breakersNumber) {
            customerData.updatePanel(panelId, panelToUpdate);
            backToViewPanelActivity();
        } else {
            showDeleteBreakerWarning(panelToUpdate);
        }
    }
    private void showDeleteBreakerWarning(final Panel newPanel) {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Breakers")
                .setMessage("This action will delete some breakers and can not be reversed. Are you sure you want to continue?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                        Log.d(TAG, "Cancel update Panel");
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Delete button
                        Log.d(TAG, "Confirm update Panel");
                        customerData.updatePanel(panelId, newPanel);
                        dialog.dismiss();
                        backToViewPanelActivity();
                    }
                })
                .create();
        alertDialog.show();
    }
    public void deletePanel(int position) {
        Log.i(TAG, "Delete Panel " + position);
        showDeletePanelWarning(position);

    }
    public void showDeletePanelWarning(final int position) {
        alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Panel")
                .setMessage("This action can not be reversed. Are you sure you want to delete this panel?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                        Log.d(TAG, "Cancel the panel deletion");
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                        Log.d(TAG, "Delete the panel");
                        customerData.removePanel(position);
                        dialog.dismiss();
                        backToElectricalActivity();
                    }
                })
                .create();
        alertDialog.show();
    }
    private void backToViewPanelActivity() {
        Context context = PanelDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        intentToStartMainActivity.putExtra("isContractor", isContractorViewingPage);
        intentToStartMainActivity.putExtra("customerId", customerData.getUid());
        if(panelId == -1) {
            if(customerData.getPanels() == null) {
                panelId = 0;
            } else {
                panelId = customerData.getPanels().size() - 1;
            }
        }
        intentToStartMainActivity.putExtra("panelId", panelId);
        startActivity(intentToStartMainActivity);
        finish();
    }
    private void backToElectricalActivity() {
        Context context = PanelDetailActivity.this;
        Class destinationClass = ElectricalActivity.class;
        Intent intentToStartElectricalActivity = new Intent(context, destinationClass);
        intentToStartElectricalActivity.putExtra("isContractor", isContractorViewingPage);
        intentToStartElectricalActivity.putExtra("customerId", customerData.getUid());
        startActivity(intentToStartElectricalActivity);
        finish();
    }

    //MARK:- Keyboard Functions
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
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

