package com.schmidthappens.markd.view_initializers;


import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.schmidthappens.markd.R;

/**
 * Created by joshua.schmidtibm.com on 10/3/17.
 */

public class ActionBarInitializer {
    private AppCompatActivity context;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private String userType;
    private Boolean isCustomerPage;
    private View.OnClickListener onEditButtonClick;

    public ActionBarInitializer(AppCompatActivity ctx, Boolean isCustomerPage, String userType, View.OnClickListener onEditButtonClick) {
        this.context = ctx;
        this.actionBar = context.getSupportActionBar();
        this.drawerLayout = (DrawerLayout)context.findViewById(R.id.main_drawer_layout);
        this.userType = userType;
        this.isCustomerPage = isCustomerPage;
        this.onEditButtonClick = onEditButtonClick;
        setTopBar();
    }

    public ActionBarInitializer(AppCompatActivity ctx, Boolean isCustomerPage, String userType) {
        this(ctx, isCustomerPage, userType, null);
    }

    // Mark: SetUp Function
    private void setTopBar() {
        if(isCustomerPage && !userType.equals("customer")) {
            setUpBackButton();
        } else {
            setUpActionBar();
            new NavigationDrawerInitializer(context, userType);
        }
    }
    private void setUpActionBar() {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
        ImageView menuButton = (ImageView)context.findViewById(R.id.burger_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        if(onEditButtonClick != null) {
            //Make edit mode accessible
            ImageView editButton = (ImageView)context.findViewById(R.id.edit_mode);
            editButton.setVisibility(View.VISIBLE);
            editButton.setClickable(true);
            editButton.setOnClickListener(onEditButtonClick);
        }
    }

    private void setUpBackButton() {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);

        ImageView menuButton = (ImageView)context.findViewById(R.id.burger_menu);
        menuButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_back));
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
    }
}
