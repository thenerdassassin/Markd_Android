package com.schmidthappens.markd.customer_subactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.contractor_user_activities.ContractorMainActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.NumberPickerUtilities;

/**
 * Created by joshua.schmidtibm.com on 10/14/17.
 */

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = "ProfileEditActivity";
    private FirebaseAuthentication authentication;

    //XML Objects
    NumberPicker userTypePicker;
    EditText email;
    EditText password;
    EditText confirmPassword;
    NumberPicker namePrefixPicker;
    EditText firstName;
    EditText lastName;
    NumberPicker maritalStatusPicker;
    NumberPicker contractorTypePicker;
    Button saveButton;

    private Boolean isNewAccount;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_profile);
        authentication = new FirebaseAuthentication(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        isNewAccount = !authentication.checkLogin();
        if(!isNewAccount) {
            setTitle("Edit Profile");
        } else {
            setUpActionBar();
        }

        initializeXMLObjects();
        processIntent(getIntent());
        email.requestFocus();
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    //Mark:- Set up functions
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView)findViewById(R.id.burger_menu);
        menuButton.setClickable(false);
        menuButton.setVisibility(View.GONE);
        ImageView nextButton = (ImageView)findViewById(R.id.edit_mode);
        if(isNewAccount) {
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setClickable(true);
            nextButton.setImageDrawable(ContextCompat.getDrawable(ProfileEditActivity.this, R.drawable.ic_action_next));
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(password.getText().toString().equals(confirmPassword.getText().toString())) {
                        attemptCreateAccount(ProfileEditActivity.this, email.getText().toString(), password.getText().toString());
                    } else {
                        Toast.makeText(ProfileEditActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            nextButton.setClickable(false);
            nextButton.setVisibility(View.GONE);
        }
    }
    private void initializeXMLObjects() {
        userTypePicker = (NumberPicker)findViewById(R.id.profile_edit_user_type);
        email = (EditText)findViewById(R.id.profile_edit_email);
        password = (EditText)findViewById(R.id.profile_edit_password);
        confirmPassword = (EditText)findViewById(R.id.profile_confirm_password);

        if(isNewAccount) {
            userTypePicker.setMinValue(0);
            userTypePicker.setMaxValue(userTypeArray.length-1);
            userTypePicker.setDisplayedValues(userTypeArray);
            userTypePicker.setVisibility(View.VISIBLE);
            userTypePicker.setOnValueChangedListener(userTypeValueChangedListener);

            password.setVisibility(View.VISIBLE);
        } else {
            userTypePicker.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
        }

        namePrefixPicker = (NumberPicker)findViewById(R.id.profile_edit_name_prefix);
        namePrefixPicker.setMinValue(0);
        namePrefixPicker.setMaxValue(namePrefixArray.length-1);
        namePrefixPicker.setDisplayedValues(namePrefixArray);

        firstName = (EditText)findViewById(R.id.profile_edit_first_name);
        lastName = (EditText)findViewById(R.id.profile_edit_last_name);

        maritalStatusPicker = (NumberPicker)findViewById(R.id.profile_edit_marital_status);
        maritalStatusPicker.setMinValue(0);
        maritalStatusPicker.setMaxValue(maritalStatusArray.length-1);
        maritalStatusPicker.setDisplayedValues(maritalStatusArray);

        contractorTypePicker = (NumberPicker)findViewById(R.id.profile_edit_contractor_type);
        contractorTypePicker.setMinValue(0);
        contractorTypePicker.setMaxValue(contractorTypeArray.length-1);
        contractorTypePicker.setDisplayedValues(contractorTypeArray);

        if(isNewAccount) {
            contractorTypePicker.setVisibility(View.GONE);
        }

        saveButton = (Button)findViewById(R.id.profile_edit_save_button);
        if(isNewAccount) {
            saveButton.setVisibility(View.GONE);
        } else {
            saveButton.setVisibility(View.VISIBLE);
            saveButton.setOnClickListener(saveButtonClickListener);
        }
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if (intent.hasExtra("email")) {
                email.setText(intent.getStringExtra("email"));
            }

            if(!isNewAccount) {
                if(intent.hasExtra("namePrefix")) {
                    NumberPickerUtilities.setPicker(namePrefixPicker, intent.getStringExtra("namePrefix"), namePrefixArray);
                }
                if(intent.hasExtra("firstName")) {
                    firstName.setText(intent.getStringExtra("firstName"));
                }
                if(intent.hasExtra("lastName")) {
                    lastName.setText(intent.getStringExtra("lastName"));
                }
                if(intent.hasExtra("maritalStatus")) {
                    //Customer
                    maritalStatusPicker.setVisibility(View.VISIBLE);
                    NumberPickerUtilities.setPicker(maritalStatusPicker, intent.getStringExtra("maritalStatus"), maritalStatusArray);
                } else {
                    //Contractor
                    maritalStatusPicker.setVisibility(View.GONE);
                }
                if(intent.hasExtra("contractorType")) {
                    //Contractor
                    contractorTypePicker.setVisibility(View.VISIBLE);
                    NumberPickerUtilities.setPicker(contractorTypePicker, intent.getStringExtra("contractorType"), contractorTypeArray);
                } else {
                    //Customer
                    contractorTypePicker.setVisibility(View.GONE);
                }
                authentication.getUserType(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userType = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.toString());
                        Toast.makeText(ProfileEditActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                if(intent.hasExtra("userType")) {
                    userType = intent.getStringExtra("userType");
                }
                if(intent.hasExtra("password")) {
                    password.setText(intent.getStringExtra("password"));
                }
            }
        }
    }

    //Mark:- Listeners
    private View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseUser user = authentication.getCurrentUser();
            String password = confirmPassword.getText().toString();
            if(password.isEmpty()) {
                Toast.makeText(ProfileEditActivity.this, "Must enter password.", Toast.LENGTH_SHORT).show();
                return;
            }
            AuthCredential credential = authentication.getAuthCredential(user.getEmail(), password);
            checkCredential(user, credential);
        }
    };

    private NumberPicker.OnValueChangeListener userTypeValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            //Need to make changes
            if(oldValue != newValue) {
                //From Home Owner to Contractor
                if(oldValue == 0 && newValue == 1) {
                    maritalStatusPicker.setVisibility(View.GONE);
                    contractorTypePicker.setVisibility(View.VISIBLE);
                }
                //From Contractor to Home Owner
                if(oldValue == 1 && newValue == 0) {
                    contractorTypePicker.setVisibility(View.GONE);
                    maritalStatusPicker.setVisibility(View.VISIBLE);
                }
            }
            userType = userTypeArray[newValue];
        }
    };

    private void attemptCreateAccount(final Activity activity, String email, String password) {
        authentication.createAccount(activity, email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "Create account success");
                    saveProfile();
                    closeActivity();
                } else {
                    Log.d(TAG, "Could not create account");
                    Log.e(TAG, task.toString());
                    Toast.makeText(activity, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkCredential(final FirebaseUser user, AuthCredential credential) {
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.i(TAG, "reauthenticate:success");
                    updateEmail(user);
                    saveProfile();
                    closeActivity();
                } else {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(ProfileEditActivity.this, "Wrong password.", Toast.LENGTH_SHORT).show();
                    } else {
                        if(task.getException() != null) {
                            Log.e(TAG, task.getException().toString());
                        }
                        Toast.makeText(ProfileEditActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void updateEmail(FirebaseUser user) {
        user.updateEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
    }
    private void saveProfile() {
        if(userType == null) {
            userType = "Home Owner";
        }
        if(userType.equalsIgnoreCase("Home Owner") || userType.equalsIgnoreCase("customer")) {
            TempCustomerData customerData = new TempCustomerData(authentication, null);
            customerData.updateProfile(
                    namePrefixArray[namePrefixPicker.getValue()],
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    maritalStatusArray[maritalStatusPicker.getValue()]
            );
        } else {
            TempContractorData contractorData = new TempContractorData(authentication.getCurrentUser().getUid(), null);
            contractorData.updateProfile(
                    namePrefixArray[namePrefixPicker.getValue()],
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    contractorTypeArray[contractorTypePicker.getValue()]
            );
        }
    }
    private void closeActivity() {
        Intent goToNextActivity;
        if(userType != null && userType.equalsIgnoreCase("Contractor")) {
            goToNextActivity = new Intent(ProfileEditActivity.this, ContractorMainActivity.class);
        } else {
            if(isNewAccount) {
                goToNextActivity = new Intent(ProfileEditActivity.this, HomeEditActivity.class);
            } else {
                goToNextActivity = new Intent(ProfileEditActivity.this, MainActivity.class);
            }
        }
        startActivity(goToNextActivity);
        finish();
    }

    //Mark:- NumberPicker Arrays
    private static final String[] userTypeArray = {
            "Home Owner",
            "Contractor"
    };
    private static final String[] namePrefixArray = {
            "Mr.",
            "Mrs.",
            "Ms.",
            "Dr.",
            "Rev."
    };

    private static final String[] maritalStatusArray = {
            "Single",
            "Married"
    };

    private static final String[] contractorTypeArray = {
            "Plumber",
            "Hvac",
            "Electrician",
            "Painter"
    };
}
