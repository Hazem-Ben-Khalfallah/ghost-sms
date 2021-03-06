package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.blacknebula.ghostsms.adapter.CustomUsersAdapter;
import com.blacknebula.ghostsms.model.SmsDto;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PermissionUtils;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListSmsActivity extends AbstractCustomToolbarActivity implements
        SwipeActionAdapter.SwipeActionListener {

    private static final int READ_SMS_REQUEST_CODE = 1;
    private static final int READ_CONTACTS_REQUEST_CODE = 2;
    private static final int RECEIVE_SMS_REQUEST_CODE = 3;

    protected SwipeActionAdapter mAdapter;
    @BindView(R.id.compose)
    Button composeButton;
    @BindView(R.id.listSms)
    ListView listSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsms);
        ButterKnife.bind(this);

        setToolbarEncryptionSwitchVisibility(false);


        if (SmsUtils.checkSmsSupport()) {
            requestReadSmsPermission();
            requestReadContactsPermission();
            requestReceiveSmsPermission();
        } else {
            Logger.warn(Logger.Type.GHOST_SMS, "SMS not support for this device!");
            ViewUtils.openUniqueActionDialog(this, R.string.sms_not_supported_title, R.string.sms_not_supported_message, this::finish);
        }

        displaySmsList();

    }

    private void displaySmsList() {
        if (!PermissionUtils.hasReadSmsPermission(this)) {
            return;
        }
        final List<SmsDto> content = SmsUtils.listSms(this);
        final CustomUsersAdapter stringAdapter = new CustomUsersAdapter(this, content);
        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(listSms);
        listSms.setAdapter(mAdapter);
        listSms.setOnItemClickListener((parent, view, position, id) -> openSmsDetails(position));

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
            case READ_CONTACTS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do what you have to do
                    displaySmsList();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Read contacts");
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

    private void requestReadContactsPermission() {
        //check API version, do nothing if API version < 23!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (!PermissionUtils.hasReadContactsPermission(this)) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ViewUtils.openDialog(this, R.string.read_contacts_request_permission_title, R.string.read_contacts_request_permission_message, new ViewUtils.OnActionListener() {
                        @Override
                        public void onPositiveClick() {
                            ActivityCompat.requestPermissions(ListSmsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
                        }

                        @Override
                        public void onNegativeClick() {
                            // permission denied, boo! but don't block app
                            Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Read contacts");
                        }
                    });

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
                }
            } else {
                //do nothing
            }
        }
    }


    private void requestReadSmsPermission() {
        //check API version, do nothing if API version < 23!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (!PermissionUtils.hasReadSmsPermission(this)) {

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

    @Override
    public boolean hasActions(int position, SwipeDirection direction) {
        return direction.isLeft() || direction.isRight();
    }

    @Override
    public boolean shouldDismiss(int position, SwipeDirection direction) {
        return direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
    }

    @Override
    public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
        for (int i = 0; i < positionList.length; i++) {
            final SwipeDirection direction = directionList[i];
            final int position = positionList[i];

            switch (direction) {
                case DIRECTION_FAR_LEFT:
                case DIRECTION_NORMAL_LEFT:
                    openSmsDetails(position);
                    break;
                case DIRECTION_FAR_RIGHT:
                case DIRECTION_NORMAL_RIGHT:
                    ViewUtils.openDialog(this, R.string.sms_remove_title, R.string.sms_remove_description, new ViewUtils.OnActionListener() {
                        @Override
                        public void onPositiveClick() {
                            removeSms(position);
                        }

                        @Override
                        public void onNegativeClick() {
                        }
                    });
                    break;
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private void removeSms(int position) {
        if (SmsUtils.isDefaultSmsApp(this)) {
            final SmsDto sms = (SmsDto) mAdapter.getItem(position);
            final boolean status = SmsUtils.removeSms(this, sms.getId());
            if (status) {
                ViewUtils.showToast(this, "Sms has been removed");
            } else {
                ViewUtils.showToast(this, "Sms could not be removed");
            }
        } else {
            ViewUtils.showToast(this, "Ghost SMS is not default sms app");
        }
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
