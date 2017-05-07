package com.blacknebula.ghostsms.activity;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blacknebula.ghostsms.R;

import java.util.List;

public class CustomUsersAdapter extends ArrayAdapter<SmsDto> {
    public CustomUsersAdapter(Context context, List<SmsDto> smsList) {
        super(context, 0, smsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final SmsDto sms = getItem(position);
        if (sms == null) {
            return convertView;
        }

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bg, parent, false);
        }
        // Lookup view for data population
        final TextView phone = (TextView) convertView.findViewById(R.id.phone);
        final TextView message = (TextView) convertView.findViewById(R.id.message);
        final TextView smsDate = (TextView) convertView.findViewById(R.id.smsDate);
        // Populate the data into the template view using the data object
        phone.setText(sms.getPhone());
        if (sms.isEncrypted()) {
            phone.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_lock, 0, 0, 0);
        } else {
            phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        message.setText(sms.getMessage());

        final long now = System.currentTimeMillis();
        smsDate.setText(DateUtils.getRelativeTimeSpanString(sms.getDate(), now, DateUtils.DAY_IN_MILLIS));

        // Return the completed view to render on screen
        return convertView;
    }
}
