package pt.isel.s1516v.ps.apiaccess.tertuliasubscription;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson.ApiSearchListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class PublicTertulia implements Parcelable {

    public final String id;
    public final String name;
    public final String subject;
    public final String location;
    public final String schedule;
    public ApiLink[] links;

    public PublicTertulia(ApiSearchListItem item) {
        id = item.id;
        name = item.name;
        subject = item.subject;
        location = item.location;
        schedule = item.schedule;
        links = item.links;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        PublicTertulia other = (PublicTertulia) obj;
        return obj instanceof PublicTertulia && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected PublicTertulia(Parcel in) {
        id = in.readString();
        name = in.readString();
        subject = in.readString();
        location = in.readString();
        schedule = in.readString();
        Parcelable[] parcelableLinks = in.readParcelableArray(ApiLink.class.getClassLoader());
        links = new ApiLink[parcelableLinks.length];
        for (int i = 0; i < parcelableLinks.length; i++)
            links[i] = (ApiLink) parcelableLinks[i];
    }

    public static final Creator<PublicTertulia> CREATOR = new Creator<PublicTertulia>() {
        @Override
        public PublicTertulia createFromParcel(Parcel in) {
            return new PublicTertulia(in);
        }

        @Override
        public PublicTertulia[] newArray(int size) {
            return new PublicTertulia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(subject);
        dest.writeString(location);
        dest.writeString(schedule);
        dest.writeParcelableArray(links, flags);
    }

    // endregion

}
