package pt.isel.s1516v.ps.apiaccess.support.raw;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Location;

public class RHome implements Parcelable {

    @com.google.gson.annotations.SerializedName("links")
    public final RHomeLinks links;

    public RHome(RHomeLinks links) {
        this.links = links != null ? links : new RHomeLinks(null, null);
    }

    public void pasteIn(RHome in) {
        links.pasteIn(in.links);
    }

// region Parcelable

    protected RHome(Parcel in) {
        links = in.readParcelable(RHomeLinks.class.getClassLoader());
    }

    public static final Creator<RHome> CREATOR = new Creator<RHome>() {
        @Override
        public RHome createFromParcel(Parcel in) {
            return new RHome(in);
        }

        @Override
        public RHome[] newArray(int size) {
            return new RHome[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(links, flags);
    }

// endregion
}
