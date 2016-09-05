/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.contentprovider;

import android.provider.BaseColumns;

public interface TertuliasSchema {

    String DB_NAME = "tertulias.db";
    int DB_VERSION = 4;
    String COL_ID = BaseColumns._ID;

    interface MyNotifications {
        String TBL_NAME = TertuliasContract.MyNotifications.RESOURCE;

        String COL_TERTULIA = TertuliasContract.MyNotifications.TERTULIA;
        String COL_TIMESTAMP = TertuliasContract.MyNotifications.TIMESTAMP;
        String COL_TAG = TertuliasContract.MyNotifications.TAG;
        String COL_MESSAGE = TertuliasContract.MyNotifications.MESSAGE;
        String COL_MYKEY = TertuliasContract.MyNotifications.MYKEY;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY" +
                ", " + COL_TERTULIA + " NUMBER NOT NULL" + // TERTULIA._ID
                ", " + COL_TIMESTAMP + " TEXT" + // see http://www.sqlite.org/lang_datefunc.html
                ", " + COL_TAG + " NUMBER" + // TAGS._ID
                ", " + COL_MESSAGE + " TEXT" +
                ", " + COL_MYKEY + " TEXT" +
//                ", CONSTRAINT fk_notif_tertulia FOREIGN KEY (" + COL_TERTULIA + ") REFERENCES " + Tertulias.TBL_NAME + "(" + COL_ID + ")" +
//                ", CONSTRAINT fk_notif_user FOREIGN KEY (" + COL_USER + ") REFERENCES " + Users.TBL_NAME + "(" + COL_ID + ")" +
//                ", CONSTRAINT fk_notif_type FOREIGN KEY (" + COL_TAG + ") REFERENCES " + EnumValues.TBL_NAME + "(" + COL_ID + ")" +
                ");";
    }

    interface Notifications {
        String TBL_NAME = TertuliasContract.Notifications.RESOURCE;

        String COL_REMOTE_ID = TertuliasContract.Notifications.REMOTE_ID;
        String COL_TERTULIA = TertuliasContract.Notifications.TERTULIA;
        String COL_TIMESTAMP = TertuliasContract.Notifications.TIMESTAMP;
        String COL_TAG = TertuliasContract.Notifications.TAG;
        String COL_MESSAGE = TertuliasContract.Notifications.MESSAGE;
        String COL_MYKEY = TertuliasContract.Notifications.MYKEY;
        String COL_ISREAD = TertuliasContract.Notifications.IS_READ;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY" +
                ", " + COL_REMOTE_ID + " NUMBER NOT NULL" + // Notifications.no_id
                ", " + COL_TERTULIA + " NUMBER NOT NULL" + // Notifications.no_tertulia
                ", " + COL_TIMESTAMP + " TEXT" + // see http://www.sqlite.org/lang_datefunc.html
                ", " + COL_TAG + " NUMBER" + // TAGS._ID
                ", " + COL_MESSAGE + " TEXT" +
                ", " + COL_MYKEY + " TEXT" +
                ", " + COL_ISREAD + " NUMBER NOT NULL DEFAULT 0" + // == 0 ? FALSE : TRUE
//                ", CONSTRAINT fk_notif_tertulia FOREIGN KEY (" + COL_TERTULIA + ") REFERENCES " + Tertulias.TBL_NAME + "(" + COL_ID + ")" +
//                ", CONSTRAINT fk_notif_user FOREIGN KEY (" + COL_USER + ") REFERENCES " + Users.TBL_NAME + "(" + COL_ID + ")" +
//                ", CONSTRAINT fk_notif_type FOREIGN KEY (" + COL_TAG + ") REFERENCES " + EnumValues.TBL_NAME + "(" + COL_ID + ")" +
                ");";
    }

}
