package com.schmidthappens.markd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.schmidthappens.markd.RecyclerViewClasses.PanelAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recList;
    private PanelAdapter panelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recList = (RecyclerView) findViewById(R.id.recycler_view);
        recList.setHasFixedSize(true);
        //Set RecyclerView Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); //I think this sets it to 2 per row?
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(gridLayoutManager);
        //Attach PanelAdapter to View
        panelAdapter = new PanelAdapter();
        recList.setAdapter(panelAdapter);

    }
}
