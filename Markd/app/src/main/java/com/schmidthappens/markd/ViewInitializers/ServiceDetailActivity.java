package com.schmidthappens.markd.ViewInitializers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.menu_option_activities.ElectricalActivity;

/**
 * Created by Josh on 8/5/2017.
 */

public class ServiceDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        SessionManager sessionManager = new SessionManager(ServiceDetailActivity.this);
        sessionManager.checkLogin();
    }

}
