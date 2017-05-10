package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.ContactUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PermissionUtils;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.pchmn.materialchips.ChipsInput;
import com.suke.widget.SwitchButton;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeActivity extends AppCompatActivity {
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
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

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

        messageLayout.setCounterEnabled(true);

        final List<ContactDto> contacts = ContactUtils.listContacts(ComposeActivity.this);
        destination.setFilterableList(contacts);

        final SwitchButton secureButton = (SwitchButton) toolbar.findViewById(R.id.secure);
        final TextView secureLabel = (TextView) toolbar.findViewById(R.id.secureLabel);
        secureButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    rsaKeyWrapper.setVisibility(View.VISIBLE);
                    rememberKey.setVisibility(View.VISIBLE);
                    secureLabel.setText("Encryption on");
                } else {
                    rsaKeyWrapper.setVisibility(View.INVISIBLE);
                    rememberKey.setVisibility(View.INVISIBLE);
                    secureLabel.setText("Encryption off");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return true;
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
    public void encrypt(View view) {
        try {
            if (!PermissionUtils.hasSendSmsPermission(this)) {
                return;
            }

            final String encryptedMessage = SmsEncryptionWrapper.encrypt(message.getText().toString());
            SmsUtils.sendSms(this, encryptedMessage);
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
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
