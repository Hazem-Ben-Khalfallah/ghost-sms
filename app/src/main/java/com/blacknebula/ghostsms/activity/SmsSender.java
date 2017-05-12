package com.blacknebula.ghostsms.activity;

import android.content.Context;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.repository.ParameterRepository;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.PermissionUtils;
import com.blacknebula.ghostsms.utils.PreferenceUtils;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;

/**
 * @author hazem
 */

public class SmsSender {
    private static final String ENCRYPTION_ENABLED = "ENCRYPTION_ENABLED";

    public static boolean isEncryptionEnabled() {
        return PreferenceUtils.getBoolean(ENCRYPTION_ENABLED, true);
    }

    public static void setEncryptionEnabled(boolean encryptionEnabled) {
        PreferenceUtils.getPreferences().edit().putBoolean(ENCRYPTION_ENABLED, encryptionEnabled).apply();
    }

    public static boolean sendSms(Context context, String phone, String messageText, String publicKeyBase64, boolean rememberKey) {
        try {
            if (!PermissionUtils.hasSendSmsPermission(GhostSmsApplication.getAppContext())) {
                return false;
            }
            if (StringUtils.isEmpty(phone)) {
                ViewUtils.showToast(GhostSmsApplication.getAppContext(), context.getString(R.string.invalid_phone_number));
                return false;
            }

            if (StringUtils.isEmpty(messageText)) {
                ViewUtils.showToast(GhostSmsApplication.getAppContext(), context.getString(R.string.message_empty_error));
                return false;
            }

            final String messageBody;
            if (isEncryptionEnabled()) {
                // encrypt message
                byte[] publicKey = StringUtils.decodeBase64(publicKeyBase64);
                messageBody = SmsEncryptionWrapper.encrypt(messageText, publicKey);
                //save public key locally
                if (rememberKey) {
                    savePublicKey(context, phone, publicKeyBase64);
                } else {
                    removePublicKey(context, phone);
                }
                Logger.info(Logger.Type.GHOST_SMS, "*** decrypted: %s, rememberKey: %s", SmsEncryptionWrapper.decrypt(messageBody), rememberKey);
            } else {
                messageBody = messageText;
            }
            // send sms
            SmsUtils.sendSms(context, phone, messageBody);
            return true;
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
        }
        return false;
    }

    private static void removePublicKey(Context context, String phone) {
        ParameterRepository.deleteParameter(context, phone);
    }

    private static void savePublicKey(Context context, String phone, String publicKeyBase64) {
        ParameterRepository.insertOrUpdateParameter(context, phone, publicKeyBase64);
    }
}
