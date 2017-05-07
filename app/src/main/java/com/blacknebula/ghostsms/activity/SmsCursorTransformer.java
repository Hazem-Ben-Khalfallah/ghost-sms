package com.blacknebula.ghostsms.activity;

import android.database.Cursor;

/**
 * @author hazem
 */

public class SmsCursorTransformer {
    public static SmsDto transform(Cursor cursor) {
        final SmsDto sms = new SmsDto();
        sms.setId(cursor.getString(cursor.getColumnIndex("_id")));
        sms.setPhone(cursor.getString(cursor.getColumnIndex("address")));
        sms.setMessage(cursor.getString(cursor.getColumnIndex("body")));
        sms.setDate(cursor.getLong(cursor.getColumnIndex("date")));
        sms.setRead(cursor.getInt(cursor.getColumnIndex("read")) > 0);
        return sms;
    }
}
