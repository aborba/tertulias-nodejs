package pt.isel.s1516v.ps.apiaccess.tertuliaedition.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertulia;

public class EdApiTertulia extends CrApiTertulia implements Parcelable {

    @com.google.gson.annotations.SerializedName("tertulia_id")
    public final int tertuliaId;

    @com.google.gson.annotations.SerializedName("role_type")
    public final String roleType;

    @com.google.gson.annotations.SerializedName("location_id")
    public final int locationId;

    public EdApiTertulia(int tertuliaId, String tertuliaName, String subject, boolean isPrivate,
                         String roleType,
                         int locationId, String locationName, String streetAddress, String zip, String city, String country,
                         String latitude, String longitude,
                         String scheduleType) {
        super(tertuliaName, subject, isPrivate,
                locationName, streetAddress, zip, city, country,
                latitude, longitude,
                scheduleType);
        this.tertuliaId = tertuliaId;
        this.locationId = locationId;
        this.roleType = roleType;
    }

//    protected abstract String toStringContribution();

    @Override
    public String toString() {
//        String contribution = toStringContribution();
        String contribution = "";
        return tertuliaName + (contribution == null ? "" : " " + contribution);
    }

    // region Parcelable

    protected EdApiTertulia(Parcel in) {
        super(in);
        tertuliaId = in.readInt();
        roleType = in.readString();
        locationId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(tertuliaId);
        out.writeString(roleType);
        out.writeInt(locationId);
    }

    public static final Creator<EdApiTertulia> CREATOR = new Creator<EdApiTertulia>() {
        @Override
        public EdApiTertulia createFromParcel(Parcel in) {
            return new EdApiTertulia(in);
        }

        @Override
        public EdApiTertulia[] newArray(int size) {
            return new EdApiTertulia[size];
        }
    };

    // endregion

}
