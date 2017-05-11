package com.blacknebula.ghostsms.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        final TextView secureLabel = (TextView) toolbar.findViewById(R.id.secureLabel);
        secureButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                secureLabel.setText("Encryption on");
            } else {
                secureLabel.setText("Encryption off");
            }
            getOnEncryptionChangeListener().onEncryptionChange(isChecked);
        });
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
