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
        secureButton.setChecked(SmsSender.isEncryptionEnabled());

        final TextView secureLabel = (TextView) toolbar.findViewById(R.id.secureLabel);
        secureButton.setOnCheckedChangeListener((view, isChecked) -> {
            SmsSender.setEncryptionEnabled(isChecked);
            if (isChecked) {
                secureLabel.setText(R.string.encryption_on);
            } else {
                secureLabel.setText(R.string.encryption_off);
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

    interface OnEncryptionChangeListener {
        void onEncryptionChange(boolean isEncryptionEnabled);
    }
}
