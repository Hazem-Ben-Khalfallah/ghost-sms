package com.blacknebula.ghostsms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.blacknebula.ghostsms.R;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OpenSmsActivity extends AppCompatActivity {

    public static final String SMS_DETAILS = "sms_details";


    @InjectView(R.id.smsDetails)
    TextView smsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensms);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        final Parcelable parcelable = intent.getParcelableExtra(SMS_DETAILS);
        final SmsDto smsDto = Parcels.unwrap(parcelable);
        smsDetails.setText(smsDto.getMessage());
    }

}
