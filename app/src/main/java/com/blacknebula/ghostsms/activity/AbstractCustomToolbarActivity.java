package com.blacknebula.ghostsms.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PermissionUtils;
import com.blacknebula.ghostsms.utils.PreferenceUtils;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.suke.widget.SwitchButton;

import butterknife.BindView;

/**
 * @author hazem
 */

public abstract class AbstractCustomToolbarActivity extends AppCompatActivity {
    private static final String ENCRYPTION_ENABLED = "ENCRYPTION_ENABLED";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.secureLayout)
    RelativeLayout secureLayout;

    @Override
    protected void onStart() {
        super.onStart();

        setSupportActionBar(toolbar);

        final SwitchButton secureButton = (SwitchButton) toolbar.findViewById(R.id.secure);
        secureButton.setChecked(isEncryptionEnabled());

        final TextView secureLabel = (TextView) toolbar.findViewById(R.id.secureLabel);
        secureButton.setOnCheckedChangeListener((view, isChecked) -> {
            PreferenceUtils.getPreferences().edit().putBoolean(ENCRYPTION_ENABLED, isChecked).apply();
            if (isChecked) {
                secureLabel.setText("Encryption on");
            } else {
                secureLabel.setText("Encryption off");
            }
            getOnEncryptionChangeListener().onEncryptionChange(isChecked);
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
                final Intent intent = new Intent(GhostSmsApplication.getAppContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    public void sendSms(String phone, String messageText, String publicKeyBase64) {
        try {
            if (!PermissionUtils.hasSendSmsPermission(GhostSmsApplication.getAppContext())) {
                return;
            }
            if (StringUtils.isEmpty(phone)) {
                ViewUtils.showToast(GhostSmsApplication.getAppContext(), "Invalid phone number", phone);
                return;
            }

            if (StringUtils.isEmpty(messageText)) {
                ViewUtils.showToast(GhostSmsApplication.getAppContext(), "Text message should not be empty");
                return;
            }

            final String messageBody;
            if (isEncryptionEnabled()) {
                byte[] publicKey = StringUtils.decodeBase64(publicKeyBase64);
                messageBody = SmsEncryptionWrapper.encrypt(messageText, publicKey);
                Logger.info(Logger.Type.GHOST_SMS, "*** decrypted: %s", SmsEncryptionWrapper.decrypt(messageBody));
            } else {
                messageBody = messageText;
            }


            SmsUtils.sendSms(this, phone, messageBody);
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
        }
    }

    public boolean isEncryptionEnabled() {
        return PreferenceUtils.getBoolean(ENCRYPTION_ENABLED, true);
    }

    protected OnEncryptionChangeListener getOnEncryptionChangeListener() {
        return isEncryptionEnabled -> {

        };
    }

    protected void setToolbarEncryptionSwitchVisibility(boolean isVisibility) {
        if (!isVisibility) {
            secureLayout.setVisibility(View.GONE);
        } else {
            secureLayout.setVisibility(View.VISIBLE);
        }
    }

    public interface OnEncryptionChangeListener {
        void onEncryptionChange(boolean isEncryptionEnabled);
    }
}
