package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiLink implements Parcelable {
    @com.google.gson.annotations.SerializedName("rel")
    public final String rel;
    @com.google.gson.annotations.SerializedName("method")
    public final String method;
    @com.google.gson.annotations.SerializedName("href")
    public final String href;

    public ApiLink(String rel, String method, String href) {
        this.rel = rel;
        this.method = method;
        this.href = href;
    }

    protected ApiLink(Parcel in) {
        rel = in.readString();
        method = in.readString();
        href = in.readString();
    }

    public static final Creator<ApiLink> CREATOR = new Creator<ApiLink>() {
        @Override
        public ApiLink createFromParcel(Parcel in) {
            return new ApiLink(in);
        }

        @Override
        public ApiLink[] newArray(int size) {
            return new ApiLink[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rel);
        dest.writeString(method);
        dest.writeString(href);
    }
}
