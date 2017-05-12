package com.blacknebula.ghostsms.database.tables;

import android.net.Uri;

import com.blacknebula.ghostsms.database.DBProvider;

/**
 * @author hazem
 */
public interface Model {
    Uri BASE_CONTENT_URI = Uri.parse("content://" + DBProvider.CONTENT_AUTHORITY);
}
