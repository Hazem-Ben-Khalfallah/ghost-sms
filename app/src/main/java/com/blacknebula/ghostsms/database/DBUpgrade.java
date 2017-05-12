package com.blacknebula.ghostsms.database;

import android.database.sqlite.SQLiteDatabase;

import com.blacknebula.ghostsms.database.tables.Parameters;
import com.blacknebula.ghostsms.database.tables.Versions;
import com.blacknebula.ghostsms.model.Version;
import com.blacknebula.ghostsms.utils.DateTimeUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.google.common.base.Optional;

public class DBUpgrade {

    public static final Integer VERSION_1 = 1;
    public static final Integer VERSION_2 = 2;
    public static final Integer CURRENT_VERSION = VERSION_2;


    /**
     * Check if DB upgrades needs to be applied
     *
     * @param db SQLiteDatabase
     */
    public static void checkForUpgrade(SQLiteDatabase db) {
        final Optional<Version> version = Versions.getVersion(db, Names.GHOST_SMS, null); // get last saved version
        if (version.isPresent()) {
            doUpgrade(db, version.get().version);
        } else {
            doCreate(db);
        }

    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    private static void doCreate(SQLiteDatabase db) {
        Logger.info(Logger.Type.GHOST_SMS, "*** execute doCreate");
        final Version version = new Version(VERSION_1, Names.GHOST_SMS, DateTimeUtils.currentDateWithoutSeconds());
        Versions.insert(db, version);
        doUpgrade(db, version.version);
    }


    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     */
    private static void doUpgrade(SQLiteDatabase db, Integer oldVersion) {
        Logger.info(Logger.Type.GHOST_SMS, "****** last version [%s]: %s", Names.GHOST_SMS, oldVersion);
        if (CURRENT_VERSION.equals(oldVersion)) {
            return;
        } else if (VERSION_1.equals(oldVersion)) {
            upgradeFrom_1_to_2(db);
        }
    }

    /**
     * upgrade database from version 1 to version 2
     *
     * @param db SQLiteDatabase
     */
    private static void upgradeFrom_1_to_2(SQLiteDatabase db) {
        Logger.info(Logger.Type.GHOST_SMS, "*** Upgrade database from version [%s] to version [%s]", VERSION_1, CURRENT_VERSION);

        //1- do migrations
        db.execSQL(Parameters.QUERY_CREATE);
        db.execSQL(Versions.QUERY_CREATE);
        //2- update application version value
        Versions.update(db, Names.GHOST_SMS, VERSION_1, VERSION_2);
    }

    /**
     * Applications names
     */
    public static class Names {
        public final static String GHOST_SMS = "ghostSms";
    }
}
