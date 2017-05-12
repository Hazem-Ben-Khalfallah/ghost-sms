package com.blacknebula.ghostsms.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.Encryptor;
import com.blacknebula.ghostsms.encryption.KeyGenerator;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PreferenceUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener bindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #bindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(bindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        bindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
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
            share(sharedText);
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
    }

    private void share(String value) {
        Logger.info(Logger.Type.GHOST_SMS, "share %s", value);
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, value);
        startActivity(Intent.createChooser(intent, "Share"));
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
