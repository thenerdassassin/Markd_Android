package com.schmidthappens.markd.account_authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.contractor_user_activities.ContractorMainActivity;
import com.schmidthappens.markd.menu_option_activities.MainActivity;

/**
 * Created by Josh on 8/9/2017.
 */

public class SplashImageActivity extends AppCompatActivity {

        /** Duration of wait **/
        private final int SPLASH_DISPLAY_LENGTH = 1000;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_splash_image);
            getSupportActionBar().hide();

        /* New Handler to start the Activity
         * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.checkLogin();
                    if(sessionManager.getUserType() != null && sessionManager.getUserType().equals("customer")) {
                        Intent mainIntent = new Intent(SplashImageActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Intent contractorMainIntent = new Intent(SplashImageActivity.this, ContractorMainActivity.class);
                        startActivity(contractorMainIntent);
                        finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
}
