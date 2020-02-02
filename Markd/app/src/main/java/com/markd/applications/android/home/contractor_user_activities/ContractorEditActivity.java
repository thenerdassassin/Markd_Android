package com.markd.applications.android.home.contractor_user_activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.data_objects.ContractorDetails;
import com.markd.applications.android.home.data_objects.TempContractorData;

/**
 * Created by Josh on 9/22/2017.
 */

public class ContractorEditActivity extends AppCompatActivity {
    private static final String TAG = "ContractorEditActivity";
    private FirebaseAuthentication authentication;
    private TempContractorData contractorData;

    EditText companyNameEditText;
    EditText telephoneEditText;
    EditText webisteEditText;
    EditText zipcodeEditText;
    Button saveContractorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_edit_view);

        authentication = new FirebaseAuthentication(this);
        contractorData = new TempContractorData(authentication, null);

        companyNameEditText = findViewById(R.id.contractor_edit_company_name);
        setEnterButtonToKeyboardDismissal(companyNameEditText);
        telephoneEditText = findViewById(R.id.contractor_edit_telephone);
        setEnterButtonToKeyboardDismissal(telephoneEditText);
        telephoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        webisteEditText = findViewById(R.id.contractor_edit_website);
        setEnterButtonToKeyboardDismissal(webisteEditText);
        zipcodeEditText = findViewById(R.id.contractor_edit_zipcode);
        setEnterButtonToKeyboardDismissal(zipcodeEditText);
        saveContractorButton = findViewById(R.id.contractor_edit_save_button);

        setTitle("Company Details");

        Intent intent = getIntent();
        initializeEditTextViews(intent);
        saveContractorButton.setOnClickListener(saveButtonClickListener);
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

    //Mark:- Click Listener
    private View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(ContractorEditActivity.this.getCurrentFocus());
            saveContractorChanges();
            goBackToContractorMainActivity();
        }
    };

    private void saveContractorChanges() {
        Log.i(TAG, "Contractor updated");
        ContractorDetails updatedContractorDetails = new ContractorDetails(
                companyNameEditText.getText().toString(),
                telephoneEditText.getText().toString(),
                webisteEditText.getText().toString(),
                zipcodeEditText.getText().toString()
        );
        contractorData.updateContractorDetails(updatedContractorDetails);
    }
    private void initializeEditTextViews(Intent intent) {
        if(intent == null) {
            Log.e(TAG, "Intent was null");
            Toast.makeText(getApplicationContext(), "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
            goBackToContractorMainActivity();
        } else {
            if (intent.hasExtra("companyName")) {
                companyNameEditText.setText(intent.getStringExtra("companyName"));
            }
            if (intent.hasExtra("telephoneNumber")) {
                telephoneEditText.setText(intent.getStringExtra("telephoneNumber"));
            }
            if (intent.hasExtra("websiteUrl")) {
                webisteEditText.setText(intent.getStringExtra("websiteUrl"));
            }
            if (intent.hasExtra("zipCode")) {
                zipcodeEditText.setText(intent.getStringExtra("zipCode"));
            }
        }
    }

    private void goBackToContractorMainActivity(){
        Intent mainActivityIntent = new Intent(getApplicationContext(), ContractorMainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    //Makes the enter button dismiss soft keyboard
    private void setEnterButtonToKeyboardDismissal(final EditText editText) {
        editText.setOnEditorActionListener((view, actionId, event) -> {
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(view);
                return true;
            }
            return false;
        });
    }

    //Hides Keyboard
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.hideSoftInputFromWindow(
                        v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
