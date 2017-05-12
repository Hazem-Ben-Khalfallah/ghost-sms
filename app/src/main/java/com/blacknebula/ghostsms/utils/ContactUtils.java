package com.blacknebula.ghostsms.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.blacknebula.ghostsms.transformer.ContactCursorTransformer;
import com.blacknebula.ghostsms.model.ContactDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hazem
 */

public class ContactUtils {

    public static ContactDto getContactName(Context context, String number) {
        if (!PermissionUtils.hasReadContactsPermission(context)) {
            return new ContactDto(number);
        }
        ContactDto contact = new ContactDto(number);
        final String selection = ContactFields.number + " =?";
        final String[] args = {number};
        final Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, selection, args, null);
        if (cursor.moveToFirst()) {
            contact = ContactCursorTransformer.transform(context, cursor);
        }
        cursor.close();
        return contact;
    }

    public static List<ContactDto> listContacts(Context context) {
        final List<ContactDto> contacts = new ArrayList<>();
        if (!PermissionUtils.hasReadContactsPermission(context)) {
            return contacts;
        }
        final Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                final ContactDto contact = ContactCursorTransformer.transform(context, cursor);
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return contacts;
    }


    public static class ContactFields {
        public static String id = ContactsContract.CommonDataKinds.Phone._ID;
        public static String displayName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY;
        public static String photoUri = ContactsContract.CommonDataKinds.Phone.PHOTO_URI;
        public static String number = ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER;
    }

}
