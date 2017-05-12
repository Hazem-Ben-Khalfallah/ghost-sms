package com.blacknebula.ghostsms.database.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.blacknebula.ghostsms.model.ApplicationParameter;

/**
 * Constants for the Parameter table of the configuration provider.
 */
public final class Parameters implements BaseColumns, Model {
    public static final String NAME = Tables.PARAMETERS;

    public static final String _NAME = "_name";
    public static final String _VALUE = "_value";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(NAME).build();

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + NAME;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + NAME;

    public static final String QUERY_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" + _ID + " INTEGER PRIMARY KEY,  " + _NAME + " TEXT, " + _VALUE + " TEXT " + ")";

    /**
     * A projection of all columns in the photos table.
     */
    public static final String[] PROJECTION_ALL = {_ID, _NAME, _VALUE};

    /**
     * The default sort order for queries containing _NAME fields.
     */
    public static final String SORT_ORDER_DEFAULT = _NAME + " ASC";

    public static Uri buildUri(long id) {
        return CONTENT_URI.buildUpon().appendPath("" + id).build();
    }

    public static <T> void insert(SQLiteDatabase db, ApplicationParameter<T> parameter) {
        final String selection = _NAME + " =? ";
        final String[] selectionArgs = new String[]{parameter.name};
        final Cursor cursor = db.query(NAME, null, selection, selectionArgs, null, null, null);

        if (cursor == null || !cursor.moveToFirst())
            db.insert(Tables.PARAMETERS, null, buildValues(parameter));

        if (cursor != null)
            cursor.close();
    }

    private static <T> ContentValues buildValues(ApplicationParameter<T> parameter) {
        ContentValues values = new ContentValues();
        values.put(_NAME, parameter.name);
        if (parameter.value.getClass().isInstance(Boolean.class))
            values.put(_VALUE, (Boolean) parameter.value);
        else if (parameter.value.getClass().isInstance(Integer.class))
            values.put(_VALUE, (Integer) parameter.value);
        else if (parameter.value.getClass().isInstance(String.class))
            values.put(_VALUE, (String) parameter.value);
        else if (parameter.value.getClass().isInstance(Long.class))
            values.put(_VALUE, (Long) parameter.value);
        else if (parameter.value.getClass().isInstance(Float.class))
            values.put(_VALUE, (Float) parameter.value);
        else if (parameter.value.getClass().isInstance(Double.class))
            values.put(_VALUE, (Double) parameter.value);
        else
            values.putNull(_VALUE);

        return values;
    }

    public static final class Names {
        public static final String MODE = "mode";
        public static final String PASSWORD = "password";
        public static final String DEMO_WATCHED = "demo_watched";
        public static final String UUID = "box-uuid";
    }
}