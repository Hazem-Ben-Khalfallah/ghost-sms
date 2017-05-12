package com.blacknebula.ghostsms.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.blacknebula.ghostsms.database.tables.Parameters;
import com.blacknebula.ghostsms.database.tables.Tables;
import com.blacknebula.ghostsms.database.tables.Versions;

/**
 * Content providers are one of the primary building blocks of Android applications, providing
 * content to applications. They encapsulate data and provide it to applications through the single
 * {@link ContentResolver} interface. A content provider is only required if you need to share
 * data between multiple applications. For example, the contacts data is used by multiple
 * applications and must be stored in a content provider. If you don't need to share data amongst
 * multiple applications you can use a database directly via
 * {@link android.database.sqlite.SQLiteDatabase}.
 * <p>
 * <p>When a request is made via
 * a {@link ContentResolver} the system inspects the authority of the given URI and passes the
 * request to the content provider registered with the authority. The content provider can interpret
 * the rest of the URI however it wants. The {@link UriMatcher} class is helpful for parsing
 * URIs.</p>
 * <p>
 * <p>The primary methods that need to be implemented are:
 * <ul>
 * <li>{@link #onCreate} which is called to initialize the provider</li>
 * <li>{@link #query} which returns data to the caller</li>
 * <li>{@link #insert} which inserts new data into the content provider</li>
 * <li>{@link #update} which updates existing data in the content provider</li>
 * <li>{@link #delete} which deletes data from the content provider</li>
 * <li>{@link #getType} which returns the MIME type of data in the content provider</li>
 * </ul></p>
 */
public class DBProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "com.blacknebula.ghostsms.provider";

    private static final int
            PARAMETERS = 300, PARAMETER_ID = 301,
            VERSIONS = 600, VERSION_ID = 601;

    private DBHelper mHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, Tables.PARAMETERS, PARAMETERS);
        matcher.addURI(CONTENT_AUTHORITY, Tables.PARAMETERS + "/#", PARAMETER_ID);
        matcher.addURI(CONTENT_AUTHORITY, Tables.VERSIONS, VERSIONS);
        matcher.addURI(CONTENT_AUTHORITY, Tables.VERSIONS + "/#", VERSION_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new DBHelper(getContext());
        DBUpgrade.checkForUpgrade(mHelper.getWritableDatabase());
        return true;
    }


    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case PARAMETERS:
                return Parameters.CONTENT_TYPE;
            case PARAMETER_ID:
                return Parameters.CONTENT_ITEM_TYPE;
            case VERSIONS:
                return Versions.CONTENT_TYPE;
            case VERSION_ID:
                return Versions.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = null;

        switch (mUriMatcher.match(uri)) {
            case PARAMETERS:
                cursor = db.query(Tables.PARAMETERS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PARAMETER_ID:
                cursor = db.query(Tables.PARAMETERS, projection, selectionWithId(selection, uri.getLastPathSegment()), selectionArgs, null, null, sortOrder);
                break;
            case VERSIONS:
                cursor = db.query(Tables.VERSIONS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VERSION_ID:
                cursor = db.query(Tables.VERSIONS, projection, selectionWithId(selection, uri.getLastPathSegment()), selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Wrong uri: " + uri);
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        Uri insertUri = null;
        long id;


        switch (mUriMatcher.match(uri)) {
            case PARAMETERS:
                id = db.insert(Tables.PARAMETERS, null, values);
                insertUri = Parameters.buildUri(id);
                break;
            case VERSIONS:
                id = db.insert(Tables.VERSIONS, null, values);
                insertUri = Versions.buildUri(id);
                break;
            default:
                throw new UnsupportedOperationException("Wrong uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return insertUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int itemDeletedCount = 0;

        switch (mUriMatcher.match(uri)) {
            case PARAMETERS:
                itemDeletedCount = db.delete(Tables.PARAMETERS, selection, selectionArgs);
                break;
            case PARAMETER_ID:
                itemDeletedCount = db.delete(Tables.PARAMETERS, selectionWithId(selection, uri.getLastPathSegment()), selectionArgs);
                break;
            case VERSIONS:
                itemDeletedCount = db.delete(Tables.VERSIONS, selection, selectionArgs);
                break;
            case VERSION_ID:
                itemDeletedCount = db.delete(Tables.VERSIONS, selectionWithId(selection, uri.getLastPathSegment()), selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Wrong uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return itemDeletedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int itemUpdatedCount = 0;

        switch (mUriMatcher.match(uri)) {
            case PARAMETERS:
                itemUpdatedCount = db.update(Tables.PARAMETERS, values, selection, selectionArgs);
                break;
            case PARAMETER_ID:
                itemUpdatedCount = db.update(Tables.PARAMETERS, values, selectionWithId(selection, uri.getLastPathSegment()), selectionArgs);
                break;
            case VERSIONS:
                itemUpdatedCount = db.update(Tables.VERSIONS, values, selection, selectionArgs);
                break;
            case VERSION_ID:
                itemUpdatedCount = db.update(Tables.VERSIONS, values, selectionWithId(selection, uri.getLastPathSegment()), selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Wrong uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return itemUpdatedCount;
    }

    private String selectionWithId(String selection, String id) {
        if (TextUtils.isEmpty(selection)) {
            selection = BaseColumns._ID + "=" + id;
        } else {
            selection += " AND " + BaseColumns._ID + "=" + id;
        }

        return selection;
    }

}
