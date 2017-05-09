package com.blacknebula.ghostsms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.activity.ContactDto;
import com.blacknebula.ghostsms.activity.SmsCursorTransformer;
import com.blacknebula.ghostsms.activity.SmsDto;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static List<SmsDto> listSms(Context context) {
        final List<SmsDto> smsList = new ArrayList<>();
        final Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://sms"), null, null, null, "date desc");

        final Set<String> threadsSet = new HashSet<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                final SmsDto sms = SmsCursorTransformer.transform(cursor);
                if (!threadsSet.contains(sms.getThreadId())) {
                    final ContactDto contact = ContactUtils.getContactName(context, sms.getPhone());
                    sms.setDisplayName(contact.getLabel());
                    sms.setPhotoUri(contact.getAvatarUriString());
                    smsList.add(sms);
                    threadsSet.add(sms.getThreadId());
                }
            } while (cursor.moveToNext());
        }

        return smsList;
    }

    public static List<SmsDto> getConversation(Context context, String threadId, Optional<Long> endTimeInMillis) {
        final List<SmsDto> smsList = new ArrayList<>();
        final String selection = String.format("%s=? AND %s<=? ", SmsFields.threadId, SmsFields.date);
        final String[] args = {threadId, Long.toString(endTimeInMillis.or(Calendar.getInstance().getTimeInMillis()))};
        final Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://sms"), null, selection, args, "date ");

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                final SmsDto smsDto = SmsCursorTransformer.transform(cursor);
                smsList.add(smsDto);
            } while (cursor.moveToNext());
        }

        return smsList;
    }

    /**
     * todo implement this method
     *
     * @param context
     * @param message
     */
    public static void sendSms(Context context, String message) {
        ViewUtils.showToast(context, message);
    }

    public static class SmsFields {
        public static String id = "_id";
        public static String threadId = "thread_id";
        public static String type = "type";
        public static String address = "address";
        public static String body = "body";
        public static String date = "date";
        public static String read = "read";
    }

    public static class SmsTypes {
        public static int TYPE_INBOX = 1;
        public static int TYPE_SENT = 2;
    }
}
