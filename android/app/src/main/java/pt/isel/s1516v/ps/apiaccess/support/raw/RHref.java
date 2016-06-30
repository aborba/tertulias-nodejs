package pt.isel.s1516v.ps.apiaccess.support.raw;

import android.os.Parcel;
import android.os.Parcelable;

public class RHref implements Parcelable {

    @com.google.gson.annotations.SerializedName("href")
    private String href;

    public RHref(String value) {
        href = value;
    }

    public String getHref() {
        return href;
    }

    public void pasteIn(RHref in) {
        href = in.getHref();
    }

    // region Parcelable

    protected RHref(Parcel in) {
        href = in.readString();
    }

    public static final Creator<RHref> CREATOR = new Creator<RHref>() {
        @Override
        public RHref createFromParcel(Parcel in) {
            return new RHref(in);
        }

        @Override
        public RHref[] newArray(int size) {
            return new RHref[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(href);
    }

    // endregion
}
