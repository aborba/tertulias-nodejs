package pt.isel.s1516v.ps.apiaccess.support.raw;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class RHomeLinks implements Parcelable {

    @com.google.gson.annotations.SerializedName("tertulias")
    public final RHref tertulias;
    @com.google.gson.annotations.SerializedName("registration")
    public final RHref registration;

    public RHomeLinks(RHref tertulias, RHref registration) {
        this.tertulias = tertulias;
        this.registration = registration;
    }

    public void exposeMap(HashMap<String, String> map) {
        MainActivity.baseRoutes.put(TertuliasApi.GET_TERTULIAS, tertulias.href);
        MainActivity.baseRoutes.put(TertuliasApi.POST_TERTULIAS, tertulias.href);
        MainActivity.baseRoutes.put(TertuliasApi.POST_REGISTRATION, registration.href);
    }

// region Parcelable

    protected RHomeLinks(Parcel in) {
        tertulias = in.readParcelable(RHref.class.getClassLoader());
        registration = in.readParcelable(RHref.class.getClassLoader());
    }

    public static final Creator<RHomeLinks> CREATOR = new Creator<RHomeLinks>() {
        @Override
        public RHomeLinks createFromParcel(Parcel in) {
            return new RHomeLinks(in);
        }

        @Override
        public RHomeLinks[] newArray(int size) {
            return new RHomeLinks[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tertulias, flags);
        dest.writeParcelable(registration, flags);
    }

// endregion
}
