package com.blacknebula.ghostsms.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
            PreferenceUtils.getPreferences().edit().putBoolean("encryptionEnabled", isChecked).apply();
            if (isChecked) {
                secureLabel.setText("Encryption on");
            } else {
                secureLabel.setText("Encryption off");
            }
            getOnEncryptionChangeListener().onEncryptionChange(isChecked);
        });
    }

    public void sendSms(String phone, String messageText) {
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
                messageBody = SmsEncryptionWrapper.encrypt(messageText);
            } else {
                messageBody = messageText;
            }

            SmsUtils.sendSms(this, phone, messageBody);
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
        }
    }

    public boolean isEncryptionEnabled() {
        return PreferenceUtils.getBoolean("encryptionEnabled", true);
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
