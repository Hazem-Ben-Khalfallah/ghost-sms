package com.blacknebula.ghostsms.activity;

import android.database.Cursor;

import com.blacknebula.ghostsms.utils.ContactUtils;

/**
 * @author hazem
 */

public class ContactCursorTransformer {
    public static ContactDto transform(Cursor cursor) {
        final ContactDto contact = new ContactDto();
        contact.setDisplayName(cursor.getString(cursor.getColumnIndex(ContactUtils.ContactFields.displayName)));
        contact.setPhotoUri(cursor.getString(cursor.getColumnIndex(ContactUtils.ContactFields.photoUri)));
        return contact;
    }
}
