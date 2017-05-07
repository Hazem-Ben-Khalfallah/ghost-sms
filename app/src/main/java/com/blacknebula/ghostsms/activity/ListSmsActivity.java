package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

public class ListSmsActivity extends ListActivity implements
        SwipeActionAdapter.SwipeActionListener {

    private static final int READ_SMS_REQUEST_CODE = 1;

    protected SwipeActionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsms);
        ButterKnife.inject(this);


        if (SmsUtils.checkSmsSupport()) {
            requestReadSmsPermission();
        } else {
            Logger.warn(Logger.Type.GHOST_SMS, "SMS not support for this device!");
            ViewUtils.openUniqueActionDialog(this, R.string.sms_not_supported_title, R.string.sms_not_supported_message, new ViewUtils.OnClickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            });
        }

        final List<SmsDto> content = listSms();
        final CustomUsersAdapter stringAdapter = new CustomUsersAdapter(this, content);
        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(getListView());
        setListAdapter(mAdapter);

        mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_SMS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do what you have to do
                    readSms();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Read sms");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void requestReadSmsPermission() {
        //check API version, do nothing if API version < 23!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ViewUtils.openDialog(this, R.string.read_sms_request_permission_title, R.string.read_sms_request_permission_message, new ViewUtils.OnActionListener() {
                        @Override
                        public void onPositiveClick() {
                            ActivityCompat.requestPermissions(ListSmsActivity.this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
                        }

                        @Override
                        public void onNegativeClick() {
                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Read SMS");
                            finish();
                        }
                    });

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
                }
            } else {
                // Permission already granted
                readSms();
            }
        }
    }

    private void readSms() {
        final Cursor cursor = getContentResolver()
                .query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx) + "\n";
                }
                Logger.info(Logger.Type.GHOST_SMS, "*** %s", msgData);
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
    }

    private List<SmsDto> listSms() {
        final List<SmsDto> smsList = new ArrayList<>();
        SmsDto sms;
        final Cursor cursor = getContentResolver()
                .query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                long date = cursor.getLong(cursor.getColumnIndex("date"));
                boolean read = cursor.getInt(cursor.getColumnIndex("read")) > 0;
                // address, date, body , seen
                sms = new SmsDto(address, body, date, read);
                smsList.add(sms);
            } while (cursor.moveToNext());
        }

        return smsList;
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Toast.makeText(
                this,
                "Clicked " + mAdapter.getItem(position),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public boolean hasActions(int position, SwipeDirection direction) {
        if (direction.isLeft()) return true;
        if (direction.isRight()) return true;
        return false;
    }

    @Override
    public boolean shouldDismiss(int position, SwipeDirection direction) {
        return direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
    }

    @Override
    public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
        for (int i = 0; i < positionList.length; i++) {
            SwipeDirection direction = directionList[i];
            int position = positionList[i];
            String dir = "";

            switch (direction) {
                case DIRECTION_FAR_LEFT:
                    dir = "Far left";
                    break;
                case DIRECTION_NORMAL_LEFT:
                    dir = "Left";
                    break;
                case DIRECTION_FAR_RIGHT:
                    dir = "Far right";
                    break;
                case DIRECTION_NORMAL_RIGHT:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
                    dir = "Right";
                    break;
            }
            Toast.makeText(
                    this,
                    dir + " swipe Action triggered on " + mAdapter.getItem(position),
                    Toast.LENGTH_SHORT
            ).show();
            mAdapter.notifyDataSetChanged();
        }
    }


}
