package com.schmidthappens.markd.customer_menu_activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.AdapterClasses.LandscapingHistoryAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.LandscapingSeason;
import com.schmidthappens.markd.data_objects.TempLandscapingData;

/**
 * Created by Josh on 6/1/2017.
 */

public class LandscapingActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    private ListView historyList;
    private FrameLayout landscapingContractor;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_landscaping_view);

        SessionManager sessionManager = new SessionManager(LandscapingActivity.this);
        sessionManager.checkLogin();

        new ActionBarInitializer(this, true, "customer");

        //Set Up PaintList
        //TODO change to http call for landscaping history
        final TempLandscapingData landscapingData = TempLandscapingData.getInstance();
        historyList = (ListView)findViewById(R.id.landscaping_history_list);
        ArrayAdapter adapter = new LandscapingHistoryAdapter(this, R.layout.landscaping_season_history_row, landscapingData.getHistory());
        historyList.setAdapter(adapter);
        historyList.setOnItemClickListener(seasonClickListener); //todo

        //Initialize Contractor Footer
        //TODO change to http call to get landscaping contractor
        landscapingContractor = (FrameLayout)findViewById(R.id.landscaping_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.landscaping_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "Greenwich Landscape Company", "203.869.1022", "http://greenwichlandscape.net/");
        landscapingContractor.addView(v);

    }

    private AdapterView.OnItemClickListener seasonClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO set onItemClick to edit season
            Log.d("Click:", "View Season");
            LandscapingSeason s = (LandscapingSeason)parent.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), s.getComments(), Toast.LENGTH_LONG).show();
        }
    };
}
