package com.schmidthappens.markd.customer_subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.AdapterClasses.ContractorListRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.TempCustomerData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joshua.schmidtibm.com on 11/8/17.
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

    Map<Double, String> zipCodeMap;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_change_contractor);

        authentication = new FirebaseAuthentication(this);
        setTitle("Change Contractor");

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
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
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
            getZipCodesByRadius(customerData.getZipcode(), milesTextView.getText().toString());
        }
    };

    private void getZipCodesByRadius(final String zipCode, final String radius) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://www.zipcodeapi.com/rest/9xTgQJatx64XJImaOJdn8DMqCNEOSsm20fiQYskwyoui6QtVzGiIk58WSMLLf8Nf";
        if(radius.equals("0")) {
            url += "/radius.json/" + zipCode + "/1/miles";
        } else {
            url += "/radius.json/" + zipCode + "/" + radius + "/miles";
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               try {
                   JSONArray zipCodes = response.getJSONArray("zip_codes");
                   Log.d(TAG, zipCodes.toString());
                   //Make sure there is not too many zipCodes
                   if(zipCodes.length() > 30) {
                       Double halfRadius = (Double.parseDouble(radius))/2;
                       getZipCodesByRadius(zipCode, halfRadius.toString());
                       return;
                   }
                   zipCodeMap = sortZipCodes(zipCodes);
                   getContractors();
               } catch (JSONException exception) {
                   Log.d(TAG, exception.toString());
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d(TAG, error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private Map<Double, String> sortZipCodes(JSONArray zipCodes) {
        try {
            Map<Double, String> zipCodeMap = new TreeMap<Double, String>();

            for (int i = 0; i < zipCodes.length(); i++) {
                JSONObject zipCode = zipCodes.getJSONObject(i);
                Double key = zipCode.getDouble("distance");
                while(zipCodeMap.containsKey(key)) {
                    key += 0.0000001;
                }
                zipCodeMap.put(key, zipCode.getString("zip_code"));
            }
            return zipCodeMap;
        } catch (JSONException exception) {
            Log.d(TAG, exception.toString());
            return Collections.emptyMap();
        }
    }

    private void getContractors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("zip_codes");
        reference.addListenerForSingleValueEvent(zipCodeReferenceListener);
    }

    ValueEventListener zipCodeReferenceListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot firebaseZipCodesSnapshot) {
            List<String> contractors = new ArrayList<>();
            String contractorType = getContractorType();

            for(Map.Entry<Double, String> zipCode: zipCodeMap.entrySet()) {
                DataSnapshot listOfContractorsAtZipCode = firebaseZipCodesSnapshot.child(zipCode.getValue());
                if(listOfContractorsAtZipCode.exists()) {
                    for(DataSnapshot contractorReference: listOfContractorsAtZipCode.getChildren()) {
                        String contractorReferenceType = contractorReference.getValue().toString();
                        if(contractorReferenceType.equals(contractorType)) {
                            contractors.add(contractorReference.getKey());
                        }
                    }
                }
            }
            Log.d(TAG, contractors.toString());
            contractorRecyclerView.setAdapter(new ContractorListRecyclerViewAdapter(ChangeContractorActivity.this, contractors));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //TODO
        }
    };

    private String getContractorType() {
        return contractorTypeArray[contractorTypePicker.getValue()];
    }

    private static final String[] contractorTypeArray = {
            "Plumber",
            "Hvac",
            "Electrician",
            "Painter",
            "Architect",
            "Builder",
            "Realtor"
    };
}
