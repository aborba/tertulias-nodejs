package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class TertuliaListItem implements Parcelable {

    public final int id;
    public final String name;
    public final String subject;
    public final boolean isPrivate;
    public final LocationCreation location;
    public Date nextEventDate;
    public final String scheduleType;
    public String scheduleDescription;
    public final String role_type;
    public int messagesCount;
    public ApiLink[] links;

    public TertuliaListItem(int id, String name, String subject, boolean isPrivate,
                            LocationCreation location,
                            Date nextEventDate,
                            String scheduleType, String scheduleDescription,
                            String role_type,
                            int messagesCount,
                            ApiLink[] links) {

        this.id = id;
        this.name = name;
        this.subject = subject;
        this.location = location;
        this.isPrivate = isPrivate;
        this.nextEventDate = nextEventDate;
        this.scheduleType = scheduleType;
        this.scheduleDescription = scheduleDescription;
        this.role_type = role_type;
        this.messagesCount = messagesCount;
        this.links = links;
    }

    public TertuliaListItem(ApiTertuliaListItem apiTertuliaListItem) {
        id = Integer.parseInt(apiTertuliaListItem.id);
        name = apiTertuliaListItem.name;
        subject = apiTertuliaListItem.subject;
        isPrivate = true;
        if (!TextUtils.isEmpty(apiTertuliaListItem.eventDate))
            try {
                apiTertuliaListItem.eventDate = apiTertuliaListItem.eventDate.replace('T', ' ').substring(0, 16 );
                nextEventDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(apiTertuliaListItem.eventDate);
            } catch (ParseException e) {
                nextEventDate = null;
            }
        location = new LocationCreation(apiTertuliaListItem);
        scheduleType = null;
        scheduleDescription = null;
        role_type = apiTertuliaListItem.role;
        messagesCount = apiTertuliaListItem.messagesCount;
        links = apiTertuliaListItem.links;
    }

    public TertuliaListItem(ApiTertuliaEdition tertulia, ApiLink[] links) {
        id = Integer.parseInt(tertulia.tr_id);
        name = tertulia.tr_name;
        subject = tertulia.tr_subject;
        nextEventDate = null;
        location = new LocationEdition(tertulia);
        scheduleType = tertulia.sc_name;
        scheduleDescription = tertulia.sc_description;
        isPrivate = tertulia.tr_isPrivate;
        role_type = tertulia.ro_name;
        messagesCount = tertulia.messagesCount;
        this.links = links;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        TertuliaListItem other = (TertuliaListItem) obj;
        return obj instanceof TertuliaListItem && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected TertuliaListItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        subject = in.readString();
        long val = in.readLong();
        nextEventDate = val == 0 ? null : new Date(val);
        location = in.readParcelable(LocationEdition.class.getClassLoader());
        scheduleType = in.readString();
        isPrivate = in.readByte() != 0;
        role_type = in.readString();
        messagesCount = in.readInt();
    }

    public static final Parcelable.Creator<TertuliaListItem> CREATOR = new Parcelable.Creator<TertuliaListItem>() {
        @Override
        public TertuliaListItem createFromParcel(Parcel in) {
            return new TertuliaListItem(in);
        }

        @Override
        public TertuliaListItem[] newArray(int size) {
            return new TertuliaListItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(subject);
        dest.writeLong(nextEventDate == null ? 0 : nextEventDate.getTime());
        dest.writeParcelable(location, flags);
        dest.writeString(scheduleType);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeString(role_type);
        dest.writeInt(messagesCount);
    }

    // endregion

}
