package com.blacknebula.ghostsms.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.blacknebula.ghostsms.activity.ContactCursorTransformer;
import com.blacknebula.ghostsms.activity.ContactDto;

/**
 * @author hazem
 */

public class ContactUtils {

    public static ContactDto getContactName(Context context, String number) {
        if (!PermissionUtils.hasReadContactsPermission(context)) {
            return new ContactDto(number, null);
        }
        ContactDto contact = new ContactDto(number, null);
        final String selection = ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " =?";
        final String[] args = {number};
        final Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, selection, args, null);
        if (cursor.moveToFirst()) {
            contact = ContactCursorTransformer.transform(cursor);
        }
        cursor.close();
        return contact;
    }


    public static class ContactFields {
        public static String displayName = "display_name";
        public static String photoUri = "photo_uri";
    }

}
