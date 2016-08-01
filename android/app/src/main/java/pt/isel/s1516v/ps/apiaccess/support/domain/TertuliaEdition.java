package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundle;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class TertuliaEdition extends TertuliaBase {

    public final int id;
    public final LocationEdition location;
    public final Role role;
    public ApiLink[] links;

    public TertuliaEdition(int id, String name, String subject, boolean isPrivate,
                           Role role,
                           LocationEdition location,
                           String scheduleType,
                           ApiLink[] links) {
        super(name, subject, isPrivate, SCHEDULES.valueOf(scheduleType));
        this.id = id;
        this.location = location;
        this.role = role;
        this.links = links;
    }

    public TertuliaEdition(ApiTertuliaEdition tertulia, ApiLink[] links) {
        super(tertulia.tr_name, tertulia.tr_subject, tertulia.tr_isPrivate, SCHEDULES.valueOf(tertulia.sc_name.toUpperCase()));
        id = Integer.parseInt(tertulia.tr_id);
        role = new Role(tertulia.ro_id, tertulia.ro_name);
        location = new LocationEdition(tertulia);
        this.links = links;
    }

    public TertuliaEdition(ApiTertuliaEditionBundle apiTertuliaEditionBundle) {
        this(apiTertuliaEditionBundle.tertulia, apiTertuliaEditionBundle.links);
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        TertuliaEdition other = (TertuliaEdition) obj;
        return obj instanceof TertuliaEdition && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected TertuliaEdition(Parcel in) {
        super(in);
        id = in.readInt();
        role = new Role(in);
        location = new LocationEdition(in);
        links = in.createTypedArray(ApiLink.CREATOR);
    }

    public static final Creator<TertuliaEdition> CREATOR = new Creator<TertuliaEdition>() {
        @Override
        public TertuliaEdition createFromParcel(Parcel in) {
            return new TertuliaEdition(in);
        }

        @Override
        public TertuliaEdition[] newArray(int size) {
            return new TertuliaEdition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(id);
        role.writeToParcel(out, flags);
        location.writeToParcel(out, flags);
        out.writeTypedArray(links, flags);
    }

    // endregion

}
