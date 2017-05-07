package com.blacknebula.ghostsms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Telephony;

import com.blacknebula.ghostsms.GhostSmsApplication;

/**
 * @author hazem
 */

public class SmsUtils {

    public static boolean checkSmsSupport() {
        final PackageManager pm = GhostSmsApplication.getAppContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) || pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA);
    }

    public static boolean markSmsAsRead(Context context, String id) {
        try {
            final ContentValues values = new ContentValues();
            values.put("read", true);
            return context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + id, null) > 0;
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error when marking an sms as read");
        }
        return false;
    }

    public static boolean removeSms(Context context, String id) {
        try {
            return context.getContentResolver().delete(Uri.parse("content://sms/" + id), null, null) > 0;
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error when marking an sms as read");
        }
        return false;
    }

    public static boolean isDefaultSmsApp(Context context) {
        return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }

    public static class SmsFields {
        public static String id = "_id";
        public static String address = "address";
        public static String body = "body";
        public static String date = "date";
        public static String read = "read";
    }
}
