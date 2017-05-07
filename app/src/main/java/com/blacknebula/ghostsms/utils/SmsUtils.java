package com.blacknebula.ghostsms.utils;

import android.content.pm.PackageManager;

import com.blacknebula.ghostsms.GhostSmsApplication;

/**
 * @author hazem
 */

public class SmsUtils {

    public static boolean checkSmsSupport() {
        final PackageManager pm = GhostSmsApplication.getAppContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) || pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA);
    }
}
