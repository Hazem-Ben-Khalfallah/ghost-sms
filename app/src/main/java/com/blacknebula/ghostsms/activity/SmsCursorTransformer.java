package com.blacknebula.ghostsms.activity;

import android.database.Cursor;

import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.SmsUtils;

/**
 * @author hazem
 */

public class SmsCursorTransformer {
    public static SmsDto transform(Cursor cursor) {
        final SmsDto sms = new SmsDto();
        sms.setId(cursor.getString(cursor.getColumnIndex(SmsUtils.SmsFields.id)));
        sms.setThreadId(cursor.getString(cursor.getColumnIndex(SmsUtils.SmsFields.threadId)));
        sms.setType(cursor.getInt(cursor.getColumnIndex(SmsUtils.SmsFields.type)));
        sms.setPhone(cursor.getString(cursor.getColumnIndex(SmsUtils.SmsFields.address)));
        sms.setMessage(cursor.getString(cursor.getColumnIndex(SmsUtils.SmsFields.body)));
        sms.setEncrypted(SmsEncryptionWrapper.isEncrypted(sms.getMessage()));
        sms.setDate(cursor.getLong(cursor.getColumnIndex(SmsUtils.SmsFields.date)));
        sms.setRead(cursor.getInt(cursor.getColumnIndex(SmsUtils.SmsFields.read)) > 0);
        return sms;
    }
}
