package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ListSmsActivity extends ListActivity implements
        SwipeActionAdapter.SwipeActionListener {

    private static final int READ_SMS_REQUEST_CODE = 1;
    private static final int RECEIVE_SMS_REQUEST_CODE = 3;

    protected SwipeActionAdapter mAdapter;
    @InjectView(R.id.compose)
    Button composeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsms);
        ButterKnife.inject(this);


        if (SmsUtils.checkSmsSupport()) {
            requestReadSmsPermission();
            requestReceiveSmsPermission();
        } else {
            Logger.warn(Logger.Type.GHOST_SMS, "SMS not support for this device!");
            ViewUtils.openUniqueActionDialog(this, R.string.sms_not_supported_title, R.string.sms_not_supported_message, new ViewUtils.OnClickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            });
        }

        displaySmsList();

    }

    private void displaySmsList() {
        if (!checkReadSmsPermission()) {
            return;
        }
        final List<SmsDto> content = listSms();
        final CustomUsersAdapter stringAdapter = new CustomUsersAdapter(this, content);
        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(getListView());
        setListAdapter(mAdapter);

        mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right_far);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_SMS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do what you have to do
                    displaySmsList();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Read sms");
                    finish();
                }
                return;
            }
            case RECEIVE_SMS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do what you have to do

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Receive sms");
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

            if (!checkReadSmsPermission()) {

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
                displaySmsList();
            }
        }
    }

    private void requestReceiveSmsPermission() {
        //check API version, do nothing if API version < 23!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ViewUtils.openDialog(this, R.string.receive_sms_request_permission_title, R.string.receive_sms_request_permission_message, new ViewUtils.OnActionListener() {
                        @Override
                        public void onPositiveClick() {
                            ActivityCompat.requestPermissions(ListSmsActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_REQUEST_CODE);
                        }

                        @Override
                        public void onNegativeClick() {
                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Receive SMS");
                            finish();
                        }
                    });

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_REQUEST_CODE);
                }
            } else {
                // Permission already granted
            }
        }
    }

    private boolean checkReadSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private List<SmsDto> listSms() {
        readSms();

        final List<SmsDto> smsList = new ArrayList<>();
        final Cursor cursor = getContentResolver()
                .query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                smsList.add(SmsCursorTransformer.transform(cursor));
            } while (cursor.moveToNext());
        }

        return smsList;
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

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        openSmsDetails(position);
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

            switch (direction) {
                case DIRECTION_FAR_LEFT:
                case DIRECTION_NORMAL_LEFT:
                    openSmsDetails(position);
                    break;
                case DIRECTION_FAR_RIGHT:
                case DIRECTION_NORMAL_RIGHT:
                    removeSms(position);
                    break;
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private void removeSms(int position) {
        final SmsDto sms = (SmsDto) mAdapter.getItem(position);
        ViewUtils.openDialog(this, R.string.sms_remove_title, R.string.sms_remove_description, new ViewUtils.OnActionListener() {
            @Override
            public void onPositiveClick() {
                //todo
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }

    private void openSmsDetails(int position) {
        final SmsDto sms = (SmsDto) mAdapter.getItem(position);
        final Intent intent = new Intent(GhostSmsApplication.getAppContext(), OpenSmsActivity.class);
        Parcelable parcelable = Parcels.wrap(sms);
        intent.putExtra(OpenSmsActivity.SMS_DETAILS, parcelable);
        startActivity(intent);
    }

    @OnClick(R.id.compose)
    public void onComposeButtonClick(View view) {
        final Intent intent = new Intent(GhostSmsApplication.getAppContext(), ComposeActivity.class);
        startActivity(intent);
    }


}
