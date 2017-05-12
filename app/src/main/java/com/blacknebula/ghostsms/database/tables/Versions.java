package com.blacknebula.ghostsms.database.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.blacknebula.ghostsms.model.Version;
import com.blacknebula.ghostsms.utils.DateTimeUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Versions implements BaseColumns, Model {

    public static final String _VERSION = "_version";
    public static final String _APPLICATION = "_application";
    public static final String _TIMESTAMP = "_timestamp";
    private static final String NAME = Tables.VERSIONS;
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(NAME).build();

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + NAME;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + NAME;

    public static final String QUERY_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" + _ID + " INTEGER PRIMARY KEY, " + _VERSION + " INTEGER, " + _APPLICATION
            + " TEXT, " + _TIMESTAMP + " BIGINT)";

    public static String QUERY_DROP = "DROP TABLE IF EXISTS " + NAME;

    public static Uri buildUri(long id) {
        return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
    }

    public static void insert(SQLiteDatabase db, Version version) {
        final String selection = Versions._VERSION + " = ? and " + Versions._APPLICATION + " = ?";
        final String[] selectionArgs = {"" + version.version, version.application};
        final Cursor cursor = db.query(NAME, null, selection, selectionArgs, null, null, null);

        if (cursor == null || !cursor.moveToFirst())
            db.insert(Tables.VERSIONS, null, buildValues(version));

        if (cursor != null)
            cursor.close();
    }

    public static int update(SQLiteDatabase db, String application, Integer oldVersion, Integer newVersion) {
        final String mWhereClause = Versions._VERSION + " = ? and " + Versions._APPLICATION + " = ?";
        final String[] mWhereArgs = {"" + oldVersion, application};
        final Version version = new Version(newVersion, application, DateTimeUtils.currentDateWithoutSeconds());
        return db.update(Tables.VERSIONS, buildValues(version), mWhereClause, mWhereArgs);
    }

    public static Optional<Version> getVersion(SQLiteDatabase db, String application, Integer version) {
        final StringBuilder mSelectionClause = new StringBuilder(Versions._APPLICATION).append(" = ?");
        final List<String> mSelectionArgs = ImmutableList.<String>builder()
                .add(application)
                .build();
        final String orderBy = Versions._VERSION + " DESC";
        if (version != null) {
            mSelectionClause.append(" and ").append(Versions._VERSION).append(" = ?");
            mSelectionArgs.add("" + version);
        }
        final Cursor mCursor = db.query(Tables.VERSIONS, null, mSelectionClause.toString(), mSelectionArgs.toArray(new String[mSelectionArgs.size()]), null, null, orderBy);
        Optional<Version> versionResult = Optional.absent();
        if (mCursor != null) {
            if (mCursor.moveToNext()) {
                final Version result = new Version();
                result.populate(mCursor);
                Logger.info(Logger.Type.GHOST_SMS, "Find version: result found: [Version: %s], [Application: %s]", result.version, result.application);
                versionResult = Optional.of(result);
            }
            mCursor.close();
        }

        return versionResult;
    }


    private static ContentValues buildValues(Version version) {
        ContentValues values = new ContentValues();
        values.put(_VERSION, version.version);
        values.put(_APPLICATION, version.application);
        values.put(_TIMESTAMP, version.timestamp);

        return values;
    }

}