package com.schmidthappens.markd.contractor_user_activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;

/**
 * Created by Josh on 9/22/2017.
 */

public class ContractorEditActivity extends AppCompatActivity {
    private static final String TAG = "ContractorEditActivity";

    EditText companyNameEditText;
    EditText telephoneEditText;
    EditText webisteEditText;
    EditText zipcodeEditText;
    Button saveContractorButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_edit_view);

        SessionManager sessionManager = new SessionManager(ContractorEditActivity.this);
        sessionManager.checkLogin();

        companyNameEditText = (EditText)findViewById(R.id.contractor_edit_company_name);
        setEnterButtonToKeyboardDismissal(companyNameEditText);
        telephoneEditText = (EditText)findViewById(R.id.contractor_edit_telephone);
        setEnterButtonToKeyboardDismissal(telephoneEditText);
        telephoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        webisteEditText = (EditText)findViewById(R.id.contractor_edit_website);
        setEnterButtonToKeyboardDismissal(webisteEditText);
        zipcodeEditText = (EditText)findViewById(R.id.contractor_edit_zipcode);
        setEnterButtonToKeyboardDismissal(zipcodeEditText);
        saveContractorButton = (Button)findViewById(R.id.contractor_edit_save_button);

        setTitle("Company Details");

        Intent intent = getIntent();
        initializeEditTextViews(intent);
        saveContractorButton.setOnClickListener(saveButtonClickListener);
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
        //TODO: change to http call to save Contractor
        TempContractorServiceData.Contractor contractor = TempContractorServiceData.getInstance().getContractor();
        contractor.setCompanyName(companyNameEditText.getText().toString());
        contractor.setTelephoneNumber(telephoneEditText.getText().toString());
        contractor.setWebsiteUrl(webisteEditText.getText().toString());
        contractor.setZipCode(zipcodeEditText.getText().toString());
    }
    private void initializeEditTextViews(Intent intent) {
        if(intent == null) {
            return;
        }
        if(intent.hasExtra("companyName")) {
            companyNameEditText.setText(intent.getStringExtra("companyName"));
        }
        if(intent.hasExtra("telephoneNumber")) {
            telephoneEditText.setText(intent.getStringExtra("telephoneNumber"));
        }
        if(intent.hasExtra("websiteUrl")) {
            webisteEditText.setText(intent.getStringExtra("websiteUrl"));
        }
        if(intent.hasExtra("zipCode")) {
            zipcodeEditText.setText(intent.getStringExtra("zipCode"));
        }
    }

    private void goBackToContractorMainActivity(){
        Intent mainActivityIntent = new Intent(getApplicationContext(), ContractorMainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

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
