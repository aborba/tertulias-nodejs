package pt.isel.pdm.g04.pf.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.receivers.ParseReceiver;

public class Notification extends Location implements Parcelable {

    private static final String TAG = Notification.class.getSimpleName();
    private String action = ParseReceiver.ACTION; // Must match intent filter in the Manifest
    private String teacherEmail;
    private String teacherName;
    private String teacherAvatarUrl;
   private int mColor = -1;
    private String location;

    public String getAction() {
        return action;
    }

    public String getTeacherAvatarUrl() {
        return teacherAvatarUrl;
    }

    public int getColor() {
        return mColor;
    }

    public Notification withLatLon(LatLng latLon) {
        return withLatLon(latLon.latitude, latLon.longitude);
    }

    public Notification withLatLon(double lat, double lon) {
        setLatitude(lat);
        setLongitude(lon);
        return this;
    }

    public Notification withTime(long time) {
        setTime(time);
        return this;
    }

    public Notification withAltitude(double altitude) {
        setAltitude(altitude);
        return this;
    }

    public Notification withAccuracy(float accurancy) {
        setAccuracy(accurancy);
        return this;
    }


    public Notification withLocation(String location) {
        this.location = location;
        try {
            mColor = Color.parseColor(location);
        } catch (NullPointerException | IllegalArgumentException e) {
            Logger.w("Invalid location: " + location);
        }
        return this;
    }


    public Notification(Cursor cursor) {
        super(TAG);
        teacherName = cursor.getString(cursor.getColumnIndex(ParseContract.Notifications.NAME));
        teacherEmail = cursor.getString(cursor.getColumnIndex(ParseContract.Notifications.EMAIL));
        teacherAvatarUrl = cursor.getString(cursor.getColumnIndex(ParseContract.Notifications.PHOTO));
        withTime(cursor.getLong(cursor.getColumnIndex(ParseContract.Notifications.TIMESTAMP)));
        withLatLon(cursor.getDouble(cursor.getColumnIndex(ParseContract.Notifications.LATITUDE)), cursor.getDouble(cursor.getColumnIndex(ParseContract.Notifications.LONGITUDE)));
        withLocation(cursor.getString(cursor.getColumnIndex(ParseContract.Notifications.LOCATION)));
    }

    public Notification(Teacher teacher) {
        super(TAG);
        if (teacher != null) {
            this.teacherEmail = teacher.getAcademicEmail();
            teacherName = teacher.getFullName();
            teacherAvatarUrl = teacher.getAvatarUrl().getSize128();
            if (TextUtils.isEmpty(teacherName)) {
                teacherName = teacher.getShortName();
            }
        }
    }

    public String getLocation() {
        return location;
    }

    public String getFriendlyLocation(Context context) {
        return Utils.getFriendlyLocation(context, location);
    }

    public LatLng getCoordinates() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(location);
        out.writeString(teacherName);
        out.writeString(teacherEmail);
        out.writeString(teacherAvatarUrl);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    // region Private Methods

    private Notification(Parcel in) {
        super(TAG);
        Location location = super.CREATOR.createFromParcel(in);
        withLatLon(location.getLatitude(), location.getLongitude());
        withAltitude(location.getAltitude());
        withAccuracy(location.getAccuracy());
        withTime(location.getTime());
        withLocation(in.readString());
        teacherName = in.readString();
        teacherEmail = in.readString();
        teacherAvatarUrl = in.readString();
    }

    // endregion
}
