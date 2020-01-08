package com.schmidthappens.markd.account_authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.contractor_user_activities.ContractorMainActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_subactivities.ProfileEditActivity;
import com.schmidthappens.markd.utilities.DatabaseResetter;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    FirebaseAuthentication authentication;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_or_register);
        setUpActionBar();

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button)findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mCreateAccountButton = (Button)findViewById(R.id.create_account);
        mCreateAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(
                        LoginActivity.this,
                        R.style.Theme_MaterialComponents_DayNight_Dialog_Alert)
                        .setItems(
                                new String[]{"Home Owner", "Contractor"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (which == 0) {
                                            createAccount("Home Owner");
                                        } else {
                                            createAccount("Contractor");
                                        }
                                    }
                                }
                        ).create().show();
            }
        });

        TextView mForgotPasswordView = (TextView)findViewById(R.id.forgot_password);
        mForgotPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String email = mEmailView.getText().toString();
                if (!isEmailValid(email)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                    mEmailView.requestFocus();
                } else {
                    (new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("An email will be sent shortly to " + email + ".")
                            .setTitle(R.string.password_email_dialog_title)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    authentication.sendPasswordResetEmail(LoginActivity.this, email);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            })
                            .create())
                            .show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onStart() {
        super.onStart();
        authentication = new FirebaseAuthentication(LoginActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if(email.equals("adminreset@gmail.com") && password.equals("reset2017")) {
                DatabaseResetter.resetDatabase();
                email = "user@gmail.com";
                password = "password";
                Log.i(TAG, "Database reset. Logging in as user@gmail.com");
            }
            showProgress(true);
            Log.d(TAG, "about to attempt sign in");
            attemptSignIn(LoginActivity.this, email, password);
        }
    }

    private void createAccount(final String accountType) {
        final Intent createAccountActivity = new Intent(this, ProfileEditActivity.class);
        createAccountActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        createAccountActivity.putExtra("isNew", true);
        createAccountActivity.putExtra("accountType", accountType);
        startActivity(createAccountActivity);
        finish();
    }

    //Mark:- Firebase Authentication methods
    private void attemptSignIn(final Activity activity, final String email, final String password) {
        authentication.signIn(activity, email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signIn:onComplete");
                if(task.isSuccessful()) {
                    authentication.getUserType(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "signIn:getUserType");
                            showProgress(false);
                            if (dataSnapshot.getValue() != null) {
                                String userType = dataSnapshot.getValue().toString();
                                Log.d(TAG, userType);
                                if(userType.equalsIgnoreCase("customer")) {
                                    final Intent goToMainActivity = new Intent(activity, MainActivity.class);
                                    startActivity(goToMainActivity);
                                    finish();
                                } else {
                                    final Intent goToMainActivity = new Intent(activity, ContractorMainActivity.class);
                                    startActivity(goToMainActivity);
                                    finish();
                                }
                            } else {
                                Log.e(TAG, "UserType is null");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            showProgress(false);
                            Log.d(TAG, "getUserType failed databaseError");
                            Toast.makeText(activity, "Unable to log in.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    showProgress(false);
                    Log.d(TAG, "Sign in failed");
                    final Exception signInFailure = task.getException();
                    if(signInFailure instanceof FirebaseAuthInvalidUserException) {
                        final FirebaseAuthInvalidUserException userException =
                                (FirebaseAuthInvalidUserException) signInFailure;
                        if(userException.getErrorCode().equalsIgnoreCase("ERROR_USER_NOT_FOUND")) {
                            Toast.makeText(
                                    activity,
                                    "This account does not yet exist. Please create an account.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else if(userException.getErrorCode().equalsIgnoreCase("ERROR_USER_DISABLED")) {
                            Toast.makeText(
                                    activity,
                                    "User is disabled. Email joshua@markdsoftware.com for more information.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } else if(signInFailure instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                                activity,
                                signInFailure.getLocalizedMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(activity, "Unable to log in.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //Mark:- Validation of inputs
    private boolean isEmailValid(String email) {
        int atPosition = email.indexOf("@");
        int dotPosition = email.lastIndexOf(".");
        return !(atPosition < 2 || dotPosition < 3 || dotPosition - atPosition < 2);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    // Mark:- SetUp Functions
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView)findViewById(R.id.burger_menu);
        menuButton.setClickable(false);
        menuButton.setVisibility(View.GONE);
    }
}

