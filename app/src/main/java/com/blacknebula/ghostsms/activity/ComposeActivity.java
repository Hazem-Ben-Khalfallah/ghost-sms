package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.dto.ContactDto;
import com.blacknebula.ghostsms.utils.ContactUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PermissionUtils;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.pchmn.materialchips.ChipsInput;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeActivity extends AbstractCustomToolbarActivity {
    private static final int SEND_SMS_REQUEST_CODE = 2;

    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.send)
    ImageButton sendButton;
    @BindView(R.id.messageLayout)
    TextInputLayout messageLayout;
    @BindView(R.id.destination)
    ChipsInput destination;
    @BindView(R.id.rsaKeyWrapper)
    FloatLabeledEditText rsaKeyWrapper;
    @BindView(R.id.rememberKey)
    CheckBox rememberKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        if (SmsUtils.checkSmsSupport()) {
            requestSendSmsPermission();
        } else {
            Logger.warn(Logger.Type.GHOST_SMS, "SMS not support for this device!");
            ViewUtils.openUniqueActionDialog(this, R.string.sms_not_supported_title, R.string.sms_not_supported_message, () -> finish());
        }

        messageLayout.setCounterEnabled(true);

        final List<ContactDto> contacts = ContactUtils.listContacts(ComposeActivity.this);
        destination.setFilterableList(contacts);
    }

    @Override
    protected OnEncryptionChangeListener getOnEncryptionChangeListener() {
        return isEncryptionEnabled -> {
            if (isEncryptionEnabled) {
                rsaKeyWrapper.setVisibility(View.VISIBLE);
                rememberKey.setVisibility(View.VISIBLE);
            } else {
                rsaKeyWrapper.setVisibility(View.INVISIBLE);
                rememberKey.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do what you have to do


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Send sms");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @OnClick(R.id.send)
    public void sendSms(View view) {
        final ContactDto destinationContact = (ContactDto) destination.getSelectedChipList().get(0);
        SmsSender.sendSms(this, destinationContact.getInfo(), message.getText().toString(), rsaKeyWrapper.getEditText().getText().toString());
    }

    private void requestSendSmsPermission() {
        //check API version, do nothing if API version < 23!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (!PermissionUtils.hasSendSmsPermission(this)) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ViewUtils.openDialog(this, R.string.send_sms_request_permission_title, R.string.send_sms_request_permission_message, new ViewUtils.OnActionListener() {
                        @Override
                        public void onPositiveClick() {
                            ActivityCompat.requestPermissions(ComposeActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                        }

                        @Override
                        public void onNegativeClick() {
                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Logger.warn(Logger.Type.GHOST_SMS, "%s: Permission Denied!", "Send SMS");
                            finish();
                        }
                    });

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                }
            } else {
                // Permission already granted
            }
        }
    }

}
