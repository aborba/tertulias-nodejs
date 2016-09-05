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

package pt.isel.s1516v.ps.apiaccess.sendmessage;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.contentprovider.ContentValuesProvider;
import pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasContract;

public class TertuliaNewMessage implements Parcelable, ContentValuesProvider<TertuliaNewMessage> {

    public final int tertulia;
    public final String message;
    public final String myKey;
    public boolean isRead;

    public TertuliaNewMessage() {
        tertulia = -1;
        myKey = message = null;
    }

    public TertuliaNewMessage(int tertulia, String message, String myKey) {
        this.tertulia = tertulia;
        this.message = message;
        this.myKey = myKey;
    }

    private static final int TERTULIA_INDEX = 0;
    private static final int MESSAGE_INDEX = 1;
    private static final int MYKEY_INDEX = 2;

    public static int[] getColumnIndexes(Cursor cursor) {
        int[] result = new int[3];
        result[TERTULIA_INDEX] = cursor.getColumnIndex(TertuliasContract.MyNotifications.TERTULIA);
        result[MESSAGE_INDEX] = cursor.getColumnIndex(TertuliasContract.MyNotifications.MESSAGE);
        result[MYKEY_INDEX] = cursor.getColumnIndex(TertuliasContract.MyNotifications.MYKEY);
        return result;
    }

    // region ContentValuesProvider

    @Override
    public TertuliaNewMessage[] getData(Cursor cursor) {
        TertuliaNewMessage[] messages = new TertuliaNewMessage[cursor.getCount()];
        int[] opaqueIndexes = getColumnIndexes(cursor);
        int i = 0;
        while (cursor.moveToNext()) {
            int tertuliaItem = cursor.getInt(opaqueIndexes[TERTULIA_INDEX]);
            String messageItem = cursor.getString(opaqueIndexes[MESSAGE_INDEX]);
            String myKeyItem = cursor.getString(opaqueIndexes[MYKEY_INDEX]);
            TertuliaNewMessage newMessage = new TertuliaNewMessage(tertuliaItem, messageItem, myKeyItem);
            messages[i++] = newMessage;
        }
        return messages;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TertuliasContract.MyNotifications.TERTULIA, tertulia);
        values.put(TertuliasContract.MyNotifications.MESSAGE, message);
        values.put(TertuliasContract.MyNotifications.MYKEY, myKey);
        return values;
    }

    // endregion

    // region Parcelable

    protected TertuliaNewMessage(Parcel in) {
        tertulia = in.readInt();
        message = in.readString();
        myKey = in.readString();
    }

    public static final Creator<TertuliaNewMessage> CREATOR = new Creator<TertuliaNewMessage>() {
        @Override
        public TertuliaNewMessage createFromParcel(Parcel in) {
            return new TertuliaNewMessage(in);
        }

        @Override
        public TertuliaNewMessage[] newArray(int size) {
            return new TertuliaNewMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(tertulia);
        out.writeString(message);
        out.writeString(myKey);
    }

    // endregion

}
