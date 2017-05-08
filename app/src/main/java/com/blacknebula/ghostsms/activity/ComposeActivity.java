package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PermissionUtils;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ComposeActivity extends AppCompatActivity {
    private static final int SEND_SMS_REQUEST_CODE = 2;

    @InjectView(R.id.message)
    EditText message;
    @InjectView(R.id.decryptedMessage)
    EditText decryptedMessage;
    @InjectView(R.id.encryptedMessage)
    EditText encryptedMessage;
    @InjectView(R.id.encryptedMessageBase64)
    EditText encryptedMessageBase64;
    @InjectView(R.id.encryptedSecretKey)
    EditText encryptedSecretKey;
    @InjectView(R.id.encrypt)
    Button encryptButton;
    @InjectView(R.id.decrypt)
    Button decryptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.inject(this);
        if (SmsUtils.checkSmsSupport()) {
            requestSendSmsPermission();
        } else {
            Logger.warn(Logger.Type.GHOST_SMS, "SMS not support for this device!");
            ViewUtils.openUniqueActionDialog(this, R.string.sms_not_supported_title, R.string.sms_not_supported_message, new ViewUtils.OnClickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            });
        }
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


    @OnClick(R.id.encrypt)
    public void encrypt(View view) {
        try {
            final String encrypted = SmsEncryptionWrapper.encrypt(message.getText().toString());
            encryptedMessageBase64.setText(encrypted);
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
        }
    }

    @OnClick(R.id.decrypt)
    public void decrypt(View view) {
        try {
            // reset
            encryptedMessage.setText("");
            encryptedSecretKey.setText("");
            decryptedMessage.setText("");
            // decrypt
            final String message = SmsEncryptionWrapper.decrypt(encryptedMessageBase64.getText().toString());
            this.decryptedMessage.setText(message);
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while decrypting a message");
        }
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
