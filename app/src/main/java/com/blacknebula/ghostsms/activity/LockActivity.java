package com.blacknebula.ghostsms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PreferenceUtils;
import com.blacknebula.ghostsms.utils.StringUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.CORRECT;
import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG;

public class LockActivity extends AppCompatActivity {

    private static String LOCK_PATTERN = "lock_pattern";

    @InjectView(R.id.pattern_lock_view)
    PatternLockView patternLockView;
    @InjectView(R.id.pattern_lock_layout)
    RelativeLayout patternLockLayout;
    @InjectView(R.id.title)
    TextView title;

    private String tmpLockPattern;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Logger.info(Logger.Type.GHOST_SMS, "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Logger.info(Logger.Type.GHOST_SMS, "Pattern progress: " +
                    PatternLockUtils.patternToString(patternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            if (pattern == null || pattern.isEmpty()) {
                return;
            }
            final String insertedPattern = PatternLockUtils.patternToMD5(patternLockView, pattern);
            Logger.info(Logger.Type.GHOST_SMS, "Pattern complete: %s", insertedPattern);
            final String lockPattern = PreferenceUtils.getString(LOCK_PATTERN, "");
            boolean isValid;
            if (StringUtils.isNotEmpty(lockPattern)) {
                isValid = validateLockPattern(insertedPattern, lockPattern);
            } else {
                isValid = initializeLockPattern(insertedPattern);
            }
            if (isValid) {
                final Intent intent = new Intent(GhostSmsApplication.getAppContext(), MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onCleared() {
            Logger.info(Logger.Type.GHOST_SMS, "Pattern has been cleared");
        }
    };

    private boolean initializeLockPattern(String insertedPattern) {
        if (StringUtils.isEmpty(tmpLockPattern)) {
            patternLockView.clearPattern();
            title.setText(R.string.repeat_pattern_lock);
            tmpLockPattern = insertedPattern;
            patternLockView.setViewMode(CORRECT);
        } else {
            if (tmpLockPattern.equals(insertedPattern)) {
                PreferenceUtils.getPreferences().edit().putString(LOCK_PATTERN, insertedPattern).apply();
                patternLockView.setViewMode(CORRECT);
                return true;
            } else {
                title.setText(R.string.new_pattern_lock);
                title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shakeanim));
                patternLockView.clearPattern();
                tmpLockPattern = null;
            }
        }
        return false;
    }

    private boolean validateLockPattern(String insertedPattern, String lockPattern) {
        if (lockPattern.equals(insertedPattern)) {
            patternLockView.setViewMode(CORRECT);
            return true;
        } else {
            patternLockView.setViewMode(WRONG);
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        ButterKnife.inject(this);

        patternLockView.addPatternLockListener(mPatternLockViewListener);
        final String lockPattern = PreferenceUtils.getString("lock_pattern", "");
        if (StringUtils.isEmpty(lockPattern)) {
            title.setText(R.string.new_pattern_lock);
        }
    }

    @OnClick(R.id.pattern_lock_layout)
    public void onParentLayoutClick(View view) {
        patternLockView.clearPattern();
    }


}
