package com.blacknebula.ghostsms.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.blacknebula.ghostsms.database.tables.Parameters;
import com.blacknebula.ghostsms.model.ApplicationParameter;
import com.blacknebula.ghostsms.utils.Logger;
import com.google.common.base.Optional;

/**
 * ConfigurationProvider is a helper class to manage data in Parameter table.
 *
 * @author hazem
 */
public class ParameterRepository {

    /**
     * Find a parameter by its name from {@link Parameters} table
     *
     * @param context Context
     * @param name    parameter name
     * @return Optional ApplicationParameter object
     */
    public static <T> Optional<ApplicationParameter<T>> getParameter(Context context, String name, Class<T> clazz) {
        final String[] mProjection =
                {
                        Parameters._ID,
                        Parameters._NAME,
                        Parameters._VALUE
                };
        // Defines a string to contain the selection clause
        // Defines selection criteria for the rows you want to update
        final String mSelectionClause = Parameters._NAME + " = ?";
        final String[] mSelectionArgs = {name};


        final Cursor mCursor = context.getContentResolver().query(
                Parameters.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, "");

        Optional<ApplicationParameter<T>> result = Optional.absent();
        if (mCursor != null) {
            if (mCursor.moveToNext()) {
                // Determine the column index of the column named "word"
                final int NAME = mCursor.getColumnIndex(Parameters._NAME);
                final int VALUE = mCursor.getColumnIndex(Parameters._VALUE);
                ApplicationParameter<T> applicationParameter;

                if (clazz.equals(Boolean.class))
                    applicationParameter = new ApplicationParameter(mCursor.getString(NAME), mCursor.getInt(VALUE) > 0);
                else if (clazz.equals(Integer.class))
                    applicationParameter = new ApplicationParameter(mCursor.getString(NAME), mCursor.getInt(VALUE));
                else if (clazz.equals(String.class))
                    applicationParameter = new ApplicationParameter(mCursor.getString(NAME), mCursor.getString(VALUE));
                else if (clazz.equals(Long.class))
                    applicationParameter = new ApplicationParameter(mCursor.getString(NAME), mCursor.getLong(VALUE));
                else if (clazz.equals(Float.class))
                    applicationParameter = new ApplicationParameter(mCursor.getString(NAME), mCursor.getFloat(VALUE));
                else if (clazz.equals(Double.class))
                    applicationParameter = new ApplicationParameter(mCursor.getString(NAME), mCursor.getDouble(VALUE));
                else
                    applicationParameter = new ApplicationParameter();


                Logger.info(Logger.Type.GHOST_SMS, "Find parameter by name: result found: [Name: %s], [Value: %s]", applicationParameter.name, applicationParameter.value);
                result = Optional.of(applicationParameter);
            }
            mCursor.close();
        }
        return result;
    }

    /**
     * Insert parameter in {@link Parameters} table
     *
     * @param context Context
     * @param name    parameter name
     * @param value   parameter value
     * @return Uri
     */
    public static <T> Uri insertParameter(Context context, String name, T value) {
        Logger.info(Logger.Type.GHOST_SMS, "insert parameter [name: %s] [value: %s]", name, value);
        final Uri mNewUri;
        // Defines an object to contain the new values to insert
        final ContentValues mNewValues = new ContentValues();
        mNewValues.put(Parameters._NAME, name);

        if (Boolean.class.isInstance(value))
            mNewValues.put(Parameters._VALUE, (Boolean) value);
        else if (Integer.class.isInstance(value))
            mNewValues.put(Parameters._VALUE, (Integer) value);
        else if (String.class.isInstance(value))
            mNewValues.put(Parameters._VALUE, (String) value);
        else if (Long.class.isInstance(value))
            mNewValues.put(Parameters._VALUE, (Long) value);
        else if (Float.class.isInstance(value))
            mNewValues.put(Parameters._VALUE, (Float) value);
        else if (Double.class.isInstance(value))
            mNewValues.put(Parameters._VALUE, (Double) value);
        else
            return null;


        mNewUri = context.getContentResolver().insert(
                Parameters.CONTENT_URI,   // the user dictionary content URI
                mNewValues                          // the values to insert
        );
        return mNewUri;
    }

    /**
     * delete parameter in {@link Parameters} table
     *
     * @param context Context
     * @param name    parameter name
     * @return Uri
     */
    public static int deleteParameter(Context context, String name) {
        Logger.info(Logger.Type.GHOST_SMS, "delete parameter [name: %s]", name);

        final String mSelectionClause = Parameters._NAME + " = ?";
        final String[] mSelectionArgs = {name};
        // Defines an object to contain the new values to insert
        final ContentValues mDeleteValues = new ContentValues();
        mDeleteValues.put(Parameters._NAME, name);

        return context.getContentResolver().delete(
                Parameters.CONTENT_URI,   // the user dictionary content URI
                mSelectionClause,
                mSelectionArgs
        );
    }

    /**
     * update parameter in {@link Parameters} table
     *
     * @param context Context
     * @param name    parameter name
     * @param value   parameter value
     * @return Uri
     */
    public static <T> int updateParameterByName(Context context, String name, T value) {
        Logger.info(Logger.Type.GHOST_SMS, "update parameter [name: %s] [value: %s]", name, value);
        // Defines a variable to contain the number of updated rows
        int mRowsUpdated = 0;

        // Defines selection criteria for the rows you want to update
        final String mSelectionClause = Parameters._NAME + " = ?";
        final String[] mSelectionArgs = {name};

        // Defines an object to contain the new values to insert
        final ContentValues mUpdateValues = new ContentValues();
        if (Boolean.class.isInstance(value))
            mUpdateValues.put(Parameters._VALUE, (Boolean) value);
        else if (Integer.class.isInstance(value))
            mUpdateValues.put(Parameters._VALUE, (Integer) value);
        else if (String.class.isInstance(value))
            mUpdateValues.put(Parameters._VALUE, (String) value);
        else if (Long.class.isInstance(value))
            mUpdateValues.put(Parameters._VALUE, (Long) value);
        else if (Float.class.isInstance(value))
            mUpdateValues.put(Parameters._VALUE, (Float) value);
        else if (Double.class.isInstance(value))
            mUpdateValues.put(Parameters._VALUE, (Double) value);
        else
            return 0;


        mRowsUpdated = context.getContentResolver().update(
                Parameters.CONTENT_URI,   // the user dictionary content URI
                mUpdateValues,                       // the columns to update
                mSelectionClause,                    // the column to select on
                mSelectionArgs                      // the value to compare to
        );
        return mRowsUpdated;
    }

    /**
     * Insert parameter in {@link Parameters} table if it does not exist.
     * If a parameter with the same name is already existent, its value will be updated.
     *
     * @param context Context
     * @param name    parameter name
     * @param value   parameter value
     */
    public static <T> void insertOrUpdateParameter(Context context, String name, T value) {
        if (getParameter(context, name, value.getClass()).isPresent())
            updateParameterByName(context, name, value);
        else
            insertParameter(context, name, value);
    }

}
