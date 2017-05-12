package com.blacknebula.ghostsms.model;

import android.database.Cursor;
import android.util.Log;

/**
 * CursorParser is a helper class to parse Sqlite cursor.
 * @author hazem
 */
public abstract class CursorParser {
    /**
     * The method should parse data from a cursor's row.
     * @param cursor Sqlite cursor representing the result set returned by a database query.
     */
    public abstract void populate(Cursor cursor);

    /**
     * This method parses a Cursor and executes le callback {@link Callback#onEachRow(T) onEachRow} method
     * for every row contained in the cursor.
     * @param cursor Sqlite cursor representing the result set returned by a database query.
     * @param clazz return type Class created from each row
     * @param callback the callback method that will be called for each row from the cursor
     * @param <T> type class that represent each row
     */
    public static <T extends CursorParser> void parseCursor(Cursor cursor, Class<T> clazz, Callback<T> callback) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        final T t = clazz.newInstance();
                        t.populate(cursor);
                        callback.onEachRow(t);
                    } catch (Exception e) {
                        Log.e("CursorParser", "Error while parsing cursor", e);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    /**
     * the implemented {@link #onEachRow(T)} will be called for each row contained in a Cursor.
     * @param <T> type class that represent each row
     */
    public interface Callback<T extends CursorParser> {
        void onEachRow(T row);
    }
}
