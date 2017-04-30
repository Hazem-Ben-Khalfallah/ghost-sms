package com.blacknebula.ghostsms.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.DecryptionResult;
import com.blacknebula.ghostsms.encryption.Decryptor;
import com.blacknebula.ghostsms.encryption.EncryptionResult;
import com.blacknebula.ghostsms.encryption.Encryptor;
import com.blacknebula.ghostsms.encryption.KeyGenerator;
import com.blacknebula.ghostsms.utils.FileUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int READ_SMS_REQUEST_CODE = 1;
    private static final int SEND_SMS_REQUEST_CODE = 2;
    private static final int RECEIVE_SMS_REQUEST_CODE = 3;

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
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        if (checkSmsSupport()) {
            //requestReadSmsPermission();
            requestSendSmsPermission();
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

    private void generateKeys() {
        if (!FileUtils.exists(KeyGenerator.PUBLIC_KEY_PATH, this) || !FileUtils.exists(KeyGenerator.PRIVATE_KEY_PATH, this)) {
            Logger.info(Logger.Type.GHOST_SMS, "generating encryption keys");
            try {
                KeyGenerator.generateRSAKeys(GhostSmsApplication.getAppContext());
            } catch (Exception e) {
                Logger.error(Logger.Type.GHOST_SMS, e, "Error while generating keys");
            }
        } else {
            Logger.info(Logger.Type.GHOST_SMS, "Encryption keys already exist");
        }
    }

    private void readSms() {
        final Cursor cursor = getContentResolver()
                .query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
                Logger.info(Logger.Type.GHOST_SMS, "*** %s", msgData);
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
    }


    @OnClick(R.id.encrypt)
    public void encrypt(View view) {
        try {
            final EncryptionResult encryptedResult = Encryptor.encrypt(message.getText().toString());
            final String json = new Gson().toJson(encryptedResult);
            final byte[] messageBase64 = StringUtils.encodeBase64(StringUtils.fromStringToBytes(json));
            encryptedMessageBase64.setText(StringUtils.fromBytesToString(messageBase64));

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
            final String emb64 = encryptedMessageBase64.getText().toString();
            if (StringUtils.isEmpty(emb64, true) || !StringUtils.isBase64Encoded(emb64)) {
                Logger.error(Logger.Type.GHOST_SMS, "value not base64 encoded");
                return;
            }
            final byte[] jsonBase64 = StringUtils.decodeBase64(emb64);
            final String json = StringUtils.fromBytesToString(jsonBase64);
            final EncryptionResult encryptedResult = new Gson().fromJson(json, EncryptionResult.class);
            encryptedMessage.setText(encryptedResult.getEncryptedMessage());
            encryptedSecretKey.setText(encryptedResult.getEncryptedSecretKey());
            final DecryptionResult decryptionResult = Decryptor.decrypt(encryptedResult.getEncryptedMessage(), encryptedResult.getEncryptedSecretKey());
            decryptedMessage.setText(decryptionResult.getMessage());
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while decrypting a message");
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
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
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

    private boolean checkSmsSupport() {
        final PackageManager pm = this.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) || pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA);
    }

    private void requestSendSmsPermission() {
        //check API version, do nothing if API version < 23!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ViewUtils.openDialog(this, R.string.send_sms_request_permission_title, R.string.send_sms_request_permission_message, new ViewUtils.OnActionListener() {
                        @Override
                        public void onPositiveClick() {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
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
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_REQUEST_CODE);
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


}
