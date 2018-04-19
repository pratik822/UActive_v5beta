package com.uactiv.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uactiv.R;

public class Exploar_Screen extends AppCompatActivity {
    ConstraintLayout buddyup_layout,pickup_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploar__screen);
        buddyup_layout=(ConstraintLayout)findViewById(R.id.buddyup_layout);

    }
}
