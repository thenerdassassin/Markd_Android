package com.schmidthappens.markd.customer_menu_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.utilities.StringUtilities;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshua.schmidtibm.com on 1/9/18.
 */

public class HelpActivity extends AppCompatActivity {
    private final static String TAG = "HelpActivity";
    private FirebaseAuthentication authentication;

    final static DatabaseReference SUPPORT_DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference("support");

    private String userEmail;

    private TextView helpText;
    private EditText userMessage;
    private Button sendButton;

    private String helpString = "We are happy to hear from the people who make Markd great, YOU! \n" +
                                "\n" +
                                "Please don't be shy to ask for help or if there is something new you want us to add let us know!\n" +
                                "\n" +
                                "Thanks again for using Markd!\n" +
                                "\n" +
                                "Sincerly,\n" +
                                "The Markd Support Team";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_help_view);
        authentication = new FirebaseAuthentication(this);
        initializeXmlObjects();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            finish();
        } else {
            userEmail = authentication.getCurrentUser().getEmail();
            authentication.getUserType(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        String userType = dataSnapshot.getValue().toString();
                        if (userType.equalsIgnoreCase("customer")) {
                            new ActionBarInitializer(HelpActivity.this, true, "customer");
                        } else {
                            new ActionBarInitializer(HelpActivity.this, false, "contractor");
                        }
                    } else {
                        somethingWentWrong("Null value from onDataChange");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    somethingWentWrong(databaseError.toString());
                }
            });
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }
    @Override
    public void onResume() {
        super.onResume();
        //do not give the EditText focus automatically when activity starts
        userMessage.clearFocus();
        helpText.requestFocus();
    }
    private void initializeXmlObjects() {
        helpText = (TextView)findViewById(R.id.help_text);
        helpText.setText(helpString);
        userMessage = (EditText)findViewById(R.id.help_message);
        sendButton = (Button)findViewById(R.id.help_send_button);
        sendButton.setOnClickListener(sendButtonClickListener);
    }

    private View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(StringUtilities.isNullOrEmpty(userMessage.getText().toString())) {
                Toast.makeText(HelpActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage();
                Toast.makeText(HelpActivity.this, "Message sent.", Toast.LENGTH_SHORT).show();
                HelpActivity.this.finish();
            }
        }
    };
    private void sendMessage() {
        final Calendar calendar = Calendar.getInstance();
        final DatabaseReference referenceToMessage = SUPPORT_DATABASE_REFERENCE
                .child(Integer.toString(calendar.get(Calendar.YEAR)))
                .child(Integer.toString(calendar.get(Calendar.MONTH) + 1))
                .child(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))).push();
        referenceToMessage.setValue(generateMessage());
    }

    private Map<String, Object> generateMessage() {
        final Map<String, Object> message = new HashMap<>();
        message.put("date", StringUtilities.getCurrentDateString());
        message.put("from", userEmail);
        message.put("message", userMessage.getText().toString());
        return message;
    }
    private void somethingWentWrong(String logMessage) {
        Log.d(TAG, logMessage);
        Toast.makeText(HelpActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        HelpActivity.this.finish();
    }


}
