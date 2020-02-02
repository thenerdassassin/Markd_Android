package com.markd.applications.android.home.customer_subactivities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markd.applications.android.home.AdapterClasses.ContractorListRecyclerViewAdapter;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.data_objects.Contractor;
import com.markd.applications.android.home.data_objects.TempCustomerData;
import com.markd.applications.android.home.utilities.ContractorUtilities;
import com.markd.applications.android.home.utilities.ContractorUpdater;
import com.markd.applications.android.home.utilities.NumberPickerUtilities;
import com.markd.applications.android.home.utilities.ZipCodeUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Josh Schmidt on 11/8/17.
 */

public class ChangeContractorActivity extends AppCompatActivity {
    private static final String TAG = "ChangeContractorActivty";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;

    NumberPicker contractorTypePicker;
    SeekBar milesSeekbar;
    TextView milesTextView;
    Button searchButton;
    RecyclerView contractorRecyclerView;
    TextView noContractorsFound;

    Map<Double, String> zipCodeMap;
    List<String> contractorReferences;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_change_contractor);

        authentication = new FirebaseAuthentication(this);
        setTitle("Find Contractor");

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
        customerData = new TempCustomerData(this, null);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
    private void initializeXMLObjects() {
        contractorTypePicker = (NumberPicker)findViewById(R.id.change_contractor_type);
        contractorTypePicker.setMinValue(0);
        contractorTypePicker.setMaxValue(contractorTypeArray.length-1);
        contractorTypePicker.setDisplayedValues(contractorTypeArray);

        milesSeekbar = (SeekBar)findViewById(R.id.change_contractor_miles_seekbar);
        milesTextView = (TextView)findViewById(R.id.change_contractor_miles_number);
        milesSeekbar.setOnSeekBarChangeListener(milesSeekbarOnChangeListener);

        searchButton = (Button)findViewById(R.id.change_contractor_save_button);
        searchButton.setOnClickListener(searchButtonClickListener);

        contractorRecyclerView = (RecyclerView)findViewById(R.id.change_contractor_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contractorRecyclerView.setLayoutManager(layoutManager);
        contractorRecyclerView.setHasFixedSize(true);

        noContractorsFound = (TextView)findViewById(R.id.no_contractors_text_view);
        noContractorsFound.setVisibility(View.INVISIBLE);
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("contractorType")) {
                NumberPickerUtilities.setPicker(contractorTypePicker, intent.getStringExtra("contractorType"), contractorTypeArray);
            }
        }
    }

    //Mark:- Listeners
    private SeekBar.OnSeekBarChangeListener milesSeekbarOnChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            Integer miles = progress * 5;
            String value = String.valueOf(miles);
            milesTextView.setText(value);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private View.OnClickListener searchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ZipCodeUtilities.getZipCodesByRadius(ChangeContractorActivity.this, customerData.getZipcode(), milesTextView.getText().toString(), successListener, errorListener);
        }
    };
    private Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                zipCodeMap = ZipCodeUtilities.sortZipCodes(response.getJSONArray("zip_codes"));
                Log.v(TAG, zipCodeMap.toString());
                FirebaseDatabase.getInstance().getReference().child("zip_codes").addListenerForSingleValueEvent(zipCodesListener);
            } catch (JSONException exception) {
                Log.d(TAG, exception.toString());
                Toast.makeText(ChangeContractorActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, error.toString());
            Toast.makeText(ChangeContractorActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        }
    };

    private ValueEventListener zipCodesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot firebaseZipCodesSnapshot) {
            contractorReferences = ContractorUtilities.getContractorsInZipCodes(zipCodeMap, getContractorType(), firebaseZipCodesSnapshot);
            Log.v(TAG, contractorReferences.toString());
            FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(usersListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, databaseError.toString());
            Toast.makeText(ChangeContractorActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        }
    };
    private ValueEventListener usersListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot usersSnapshot) {
            List<Contractor> contractors = ContractorUtilities
                    .getContractorsFromReferences(contractorReferences, usersSnapshot);
            if(contractors.size() > 0) {
                noContractorsFound.setVisibility(View.INVISIBLE);
            } else {
                noContractorsFound.setVisibility(View.VISIBLE);
            }
            contractorRecyclerView.setAdapter(
                    new ContractorListRecyclerViewAdapter(
                            ChangeContractorActivity.this,
                            contractors,
                            contractorReferences,
                            new UpdateContractorListener()
                    ));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, databaseError.toString());
            Toast.makeText(ChangeContractorActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        }
    };

    public class UpdateContractorListener implements ContractorUpdater {
        public void update(String contractorReference) {
            customerData.updateContractor(getContractorType(), contractorReference);
            finish();
        }
    }

    private String getContractorType() {
        return contractorTypeArray[contractorTypePicker.getValue()];
    }

    private static final String[] contractorTypeArray = {
            "Plumber",
            "Hvac",
            "Electrician",
            "Painter"
    };
}
