package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;

public class Role implements Parcelable {

    public final int id;
    public final String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(String id, String name) {
        this(Integer.parseInt(id), name);
    }

    public Role(ApiTertuliaEdition core) {
        id = Integer.parseInt(core.ro_id);
        name = core.ro_name;
    }

    public static final Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        @Override
        public Role[] newArray(int size) {
            return new Role[size];
        }
    };

    private String compose(String separator, String begin, String end) {
        return TextUtils.isEmpty(begin) ? end : begin + separator + end;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Role other = (Role) obj;
        return obj instanceof Role &&
                other.id == id &&
                other.name.equals(name);
    }

    // region Parcelable

    protected Role(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
    }

    // endregion
}
