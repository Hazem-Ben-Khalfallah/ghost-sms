package com.blacknebula.ghostsms.transformer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.model.ContactDto;
import com.blacknebula.ghostsms.utils.ContactUtils;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;

/**
 * @author hazem
 */

public class ContactCursorTransformer {
    public static ContactDto transform(Context context, Cursor cursor) {
        final ContactDto contact = new ContactDto();
        contact.setId(cursor.getString(cursor.getColumnIndex(ContactUtils.ContactFields.id)));
        contact.setLabel(cursor.getString(cursor.getColumnIndex(ContactUtils.ContactFields.displayName)));
        contact.setInfo(cursor.getString(cursor.getColumnIndex(ContactUtils.ContactFields.number)));
        final String uri = cursor.getString(cursor.getColumnIndex(ContactUtils.ContactFields.photoUri));
        if (StringUtils.isNotEmpty(uri)) {
            contact.setAvatarUriString(uri);
            contact.setAvatarUri(Uri.parse(uri));
            ViewUtils.toBitmap(context, contact.getAvatarUri(), BitmapFactory.decodeResource(context.getResources(), R.drawable.circle));
        }
        return contact;
    }
}
