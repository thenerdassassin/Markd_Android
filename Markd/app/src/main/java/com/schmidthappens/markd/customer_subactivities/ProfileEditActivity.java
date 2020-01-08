package com.schmidthappens.markd.customer_subactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.schmidthappens.markd.AdapterClasses.CreateProfileRecyclerViewAdapter;
import com.schmidthappens.markd.AdapterClasses.EditProfileRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.contractor_user_activities.ContractorMainActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by joshua.schmidtibm.com on 10/14/17.
 */

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = "ProfileEditActivity";
    private FirebaseAuthentication authentication;

    public Boolean isNewAccount;
    public String userType;
    public String email;
    public String namePrefix;
    public String firstName;
    public String lastName;
    public String maritalStatus;
    public String contractorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_recycler);
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

        processIntent(getIntent());
        initializeXMLObjects();
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
        ImageView menuButton = findViewById(R.id.burger_menu);
        menuButton.setClickable(false);
        menuButton.setVisibility(View.GONE);

        final ImageView nextButton = findViewById(R.id.edit_mode);
        nextButton.setClickable(false);
        nextButton.setVisibility(View.GONE);
    }
    private void initializeXMLObjects()  {
        Log.d(TAG, "initializeXMLObjects");
        final RecyclerView recyclerView = findViewById(R.id.edit_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        if(isNewAccount) {
            recyclerView.setAdapter(new CreateProfileRecyclerViewAdapter(this));
        } else if(maritalStatus != null) {
            userType = "customer";
            recyclerView.setAdapter(new EditProfileRecyclerViewAdapter(this));
        } else if(contractorType != null) {
            userType = "contractor";
            recyclerView.setAdapter(new EditProfileRecyclerViewAdapter(this));
        }
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.getBooleanExtra("isNew", false)) {
                isNewAccount = true;
                userType = intent.getStringExtra("accountType");
                return;
            }
            if (intent.hasExtra("email")) {
                email = intent.getStringExtra("email");
            }
            if(intent.hasExtra("namePrefix")) {
                namePrefix = intent.getStringExtra("namePrefix");
            }
            if(intent.hasExtra("firstName")) {
                firstName = intent.getStringExtra("firstName");
            }
            if(intent.hasExtra("lastName")) {
                lastName = intent.getStringExtra("lastName");
            }
            if(intent.hasExtra("maritalStatus")) {
                maritalStatus = intent.getStringExtra("maritalStatus");
            }
            if(intent.hasExtra("contractorType")) {
                contractorType = intent.getStringExtra("contractorType");
            }
        }
    }

    public void updateUser(final String password)  {
        final FirebaseUser user = authentication.getCurrentUser();
        final AuthCredential credential = authentication
                .getAuthCredential(user.getEmail(), password);
        authenticateUser(user, credential);
    }
    public void createUser(final String password) {
        attemptCreateAccount(this, email, password);
    }

    //Mark:- Listeners
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
                    final Exception createAccountFailure = task.getException();
                    if(createAccountFailure instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(activity, "User already exists.", Toast.LENGTH_SHORT).show();
                    } else if(createAccountFailure instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(activity, "Password is weak.", Toast.LENGTH_SHORT).show();
                    } else if(createAccountFailure instanceof FirebaseAuthInvalidCredentialsException) {
                        if(createAccountFailure.getMessage()
                                .contains("email address is badly formatted")) {
                            Toast.makeText(activity,
                                    "The email address is badly formatted.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void authenticateUser(final FirebaseUser user, final AuthCredential credential) {
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.i(TAG, "reauthenticate:success");
                    updateProfile(user);
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
    private void updateProfile(final FirebaseUser user) {
        saveProfile();
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email updated.");
                            closeActivity();
                        }
                    }
                });
    }
    private void saveProfile() {
        if(userType == null) {
            userType = "Home Owner";
        }
        if(userType.equalsIgnoreCase("Home Owner") || userType.equalsIgnoreCase("customer")) {
            final TempCustomerData customerData = new TempCustomerData(authentication, null);
            customerData.updateProfile(namePrefix, firstName, lastName, maritalStatus);
        } else {
            final TempContractorData contractorData = new TempContractorData(
                    authentication.getCurrentUser().getUid(), null);
            contractorData.updateProfile(namePrefix, firstName, lastName, contractorType);
        }
    }
    private void closeActivity() {
        Intent goToNextActivity;
        if(userType != null && userType.equalsIgnoreCase("Contractor")) {
            goToNextActivity = new Intent(ProfileEditActivity.this, ContractorMainActivity.class);
        } else {
            goToNextActivity = new Intent(ProfileEditActivity.this, MainActivity.class);
        }
        startActivity(goToNextActivity);
        finish();
    }
}
