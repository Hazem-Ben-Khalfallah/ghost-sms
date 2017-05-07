package com.blacknebula.ghostsms.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blacknebula.ghostsms.R;

import butterknife.ButterKnife;

public class OpenSmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensms);
        ButterKnife.inject(this);

    }

}
