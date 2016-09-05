package pt.isel.pdm.g04.pf.data.parse.provider;

import android.provider.BaseColumns;

/**
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */

public interface ParseDbSchema {
    String DB_NAME = "parse.db";
    int DB_VERSION = 9;
    String COL_ID = BaseColumns._ID;

    interface Subscriptions {
        String TBL_NAME = ParseContract.Subscriptions.RESOURCE;
        String COL_EMAIL = ParseContract.Subscriptions.EMAIL;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + COL_EMAIL + " TEXT UNIQUE NOT NULL" +
                ");";
        String DDL_DROP_UNIQUE_INDEX = "DROP INDEX IF EXISTS uc_subscriptions;";
        String DDL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX uc_subscriptions ON " +
                Subscriptions.TBL_NAME + "(" + COL_EMAIL + ");";
    }

    interface Locations {
        String TBL_NAME = ParseContract.Locations.RESOURCE;
        String COL_EMAIL = ParseContract.Locations.EMAIL;
        String COL_LATITUDE = ParseContract.Locations.LATITUDE;
        String COL_LONGITUDE = ParseContract.Locations.LONGITUDE;
        String COL_LOCATION = ParseContract.Locations.LOCATION;
        String COL_TIMESTAMP = ParseContract.Locations.TIMESTAMP;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + COL_EMAIL + " TEXT NOT NULL" +
                ", " + COL_LATITUDE + " NUMBER NOT NULL" +
                ", " + COL_LONGITUDE + " NUMBER NOT NULL" +
                ", " + COL_LOCATION + " TEXT" +
                ", " + COL_TIMESTAMP + " NUMBER NOT NULL" +
                ", CONSTRAINT fkc_locations_subscriptions_email FOREIGN KEY(" + COL_EMAIL +
                ") REFERENCES " + Subscriptions.TBL_NAME + "(" + Subscriptions.COL_EMAIL + ")" +
                ");";
    }

    interface Notifications {
        String TBL_NAME = ParseContract.Notifications.RESOURCE;
        String COL_NAME = ParseContract.Notifications.NAME;
        String COL_EMAIL = ParseContract.Notifications.EMAIL;
        String COL_LATITUDE = ParseContract.Notifications.LATITUDE;
        String COL_LONGITUDE = ParseContract.Notifications.LONGITUDE;
        String COL_LOCATION = ParseContract.Locations.LOCATION;
        String COL_PHOTO = ParseContract.Notifications.PHOTO;
        String COL_TIMESTAMP = ParseContract.Notifications.TIMESTAMP;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + COL_EMAIL + " TEXT UNIQUE NOT NULL" +
                ", " + COL_NAME + " TEXT NOT NULL" +
                ", " + COL_LATITUDE + " NUMBER NOT NULL" +
                ", " + COL_LONGITUDE + " NUMBER NOT NULL" +
                ", " + COL_LOCATION + " TEXT" +
                ", " + COL_PHOTO + " TEXT" +
                 ", " + COL_TIMESTAMP + " NUMBER NOT NULL" +
                ", CONSTRAINT fkc_locations_subscriptions_email FOREIGN KEY(" + COL_EMAIL +
                ") REFERENCES " + Subscriptions.TBL_NAME + "(" + Subscriptions.COL_EMAIL + ")" +
                ");";
    }

}
