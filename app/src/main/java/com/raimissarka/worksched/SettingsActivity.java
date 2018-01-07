package com.raimissarka.worksched;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        FrameLayout mEditShiftsButton = (FrameLayout) findViewById(R.id.fl_edit_shifts);
        FrameLayout mEditWorkersButton = (FrameLayout) findViewById(R.id.fl_edit_workers);
        FrameLayout mEditEmploymentsButton = (FrameLayout) findViewById(R.id.fl_edit_employments);

        mEditShiftsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditShiftsActivity.class);
                startActivity(intent);
            }
        });

        mEditWorkersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditWorkersActivity.class);
                startActivity(intent);
            }
        });

        mEditEmploymentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditEmploymentsActivity.class);
                startActivity(intent);
            }
        });

    }

}
