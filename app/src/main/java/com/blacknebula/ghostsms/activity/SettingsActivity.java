package com.blacknebula.ghostsms.activity;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.Encryptor;
import com.blacknebula.ghostsms.encryption.KeyGenerator;
import com.blacknebula.ghostsms.utils.ContactUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PreferenceUtils;
import com.blacknebula.ghostsms.utils.ThreadUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.CORRECT;
import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final int MAX_ATTEMPTS = 3;

    private Dialog dialog;
    private PatternLockView patternLockView;
    private TextView title;
    private String tmpLockPattern;
    private int step = 1;
    private Animation shakeAnimation;
    private int unlockAttempts = 0;
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            // do nothing
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            // do nothing
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            if (pattern == null || pattern.isEmpty()) {
                return;
            }
            final String insertedPattern = PatternLockUtils.patternToSha1(patternLockView, pattern);
            Logger.info(Logger.Type.GHOST_SMS, "Pattern complete: %s", insertedPattern);
            final String lockPattern = PreferenceUtils.getString(ApplicationPreferences.LOCK_PATTERN, "");

            boolean isValid;
            if (step == 1) {
                isValid = validateLockPattern(insertedPattern, lockPattern);
                if (isValid) {
                    title.setText(R.string.new_pattern_lock);
                    step = 2;
                    isValid = false;
                }
            } else {
                isValid = initializeLockPattern(insertedPattern);
            }
            ThreadUtils.delay(500, () -> patternLockView.clearPattern());

            if (isValid) {
                dialog.dismiss();
            }
        }

        @Override
        public void onCleared() {
            // do nothing
        }

    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private boolean validateLockPattern(String insertedPattern, String lockPattern) {
        if (lockPattern.equals(insertedPattern)) {
            patternLockView.setViewMode(CORRECT);
            return true;
        } else {
            unlockAttempts++;
            if (unlockAttempts >= MAX_ATTEMPTS) {
                ThreadUtils.delay(500, () -> dialog.dismiss());
            }
            patternLockView.setViewMode(WRONG);
            title.startAnimation(shakeAnimation);

        }

        return false;
    }

    private boolean initializeLockPattern(String insertedPattern) {
        if (step == 2) {
            title.setText(R.string.repeat_pattern_lock);
            tmpLockPattern = insertedPattern;
            step = 3;
        } else {
            if (tmpLockPattern.equals(insertedPattern)) {
                patternLockView.setViewMode(CORRECT);
                title.setText(R.string.unlock_app);
                PreferenceUtils.getPreferences().edit().putString(ApplicationPreferences.LOCK_PATTERN, insertedPattern).apply();
                return true;
            } else {
                title.setText(R.string.new_pattern_lock);
                title.startAnimation(shakeAnimation);
                patternLockView.setViewMode(WRONG);
                tmpLockPattern = null;
                step = 2;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_settings);
        try {
            // init public key preference
            initialisePublicRsaPreference();
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error when retrieving Public RSA key");
        }

        // share public key
        final Preference sharePublicKeysPreference = findPreference("share_public_rsa_key");
        sharePublicKeysPreference.setOnPreferenceClickListener(preference -> {
            final String sharedText = String.format("Here is my public key \n%s", PreferenceUtils.getString("public_rsa", ""));
            ContactUtils.share(SettingsActivity.this, sharedText);
            return true;
        });

        // init reset RSA keys preference
        final Preference resetRsaKeysPreference = findPreference("reset_rsa_keys");
        resetRsaKeysPreference.setOnPreferenceClickListener(preference -> {
            ViewUtils.openDialog(SettingsActivity.this, R.string.reset_rsa_keys_title, R.string.reset_rsa_keys_confirmation, new ViewUtils.OnActionListener() {
                @Override
                public void onPositiveClick() {
                    try {
                        KeyGenerator.generateRSAKeys(GhostSmsApplication.getAppContext());
                        initialisePublicRsaPreference();
                        ViewUtils.showToast(GhostSmsApplication.getAppContext(), "Encryption keys has been regenerated");
                    } catch (Exception e) {
                        Logger.error(Logger.Type.GHOST_SMS, e, "Error when regenerating RSA keys");
                    }
                }

                @Override
                public void onNegativeClick() {
                    // do nothing
                }
            });

            return true;
        });

        // init change lock pattern
        final Preference lockPatternPreference = findPreference("change_pattern_lock");
        lockPatternPreference.setOnPreferenceClickListener(preference -> {
            unlockAttempts = 0;
            step = 1;

            // open custom
            dialog = new Dialog(SettingsActivity.this);
            dialog.setContentView(R.layout.lock_pattern_fragment);

            patternLockView = (PatternLockView) dialog.findViewById(R.id.pattern_lock_view);
            patternLockView.addPatternLockListener(mPatternLockViewListener);

            title = (TextView) dialog.findViewById(R.id.title);
            title.setText(R.string.current_pattern_lock);
            shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shakeanim);


            final Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
            cancelButton.setOnClickListener(b -> dialog.dismiss());
            dialog.show();
            return true;
        });
    }

    @SuppressWarnings("deprecation")
    private void initialisePublicRsaPreference() throws InvalidKeySpecException, NoSuchAlgorithmException {
        final Preference publicRsaPreference = findPreference("public_rsa");
        final String publicKey = Encryptor.getSenderPublic();
        publicRsaPreference.setSummary(String.format("Click to copy\n%s", publicKey));
        PreferenceUtils.getPreferences().edit().putString("public_rsa", publicKey).apply();
        publicRsaPreference.setOnPreferenceClickListener(preference -> {
            // copy to keyboard
            ViewUtils.copyToClipboard(SettingsActivity.this, "Public Key", publicKey);
            return true;
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }

}
