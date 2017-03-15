package com.schmidthappens.markd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Josh on 3/8/2017.
 */

public class BreakerDetailActivity extends AppCompatActivity {
    private TextView breaker_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breaker_detail);
        breaker_detail = (TextView)findViewById(R.id.breaker_description);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity != null) {
            if(intentThatStartedThisActivity.hasExtra("breaker_desc")) {
                breaker_detail.setText(intentThatStartedThisActivity.getStringExtra("breaker_desc"));
            }
        }
    }
}
