package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import pt.isel.s1516v.ps.apiaccess.R;

public class CrUiTertulia implements Parcelable {

    public final String name;
    public final String subject;
    public final boolean isPrivate;
    public final CrUiLocation crUiLocation;
    public final int scheduleType;
    public final CrUiSchedule crUiSchedule;

    public CrUiTertulia(String name, String subject,
                        CrUiLocation crUiLocation,
                        int scheduleType, CrUiSchedule crUiSchedule,
                        boolean isPrivate) {
        this.name = name;
        this.subject = subject;
        this.crUiLocation = crUiLocation;
        this.scheduleType = scheduleType;
        this.crUiSchedule = crUiSchedule;
        this.isPrivate = isPrivate;
    }

    public CrUiTertulia(TextView titleView, TextView subjectView,
                        TextView locationView, TextView addressView, TextView zipView, TextView cityView, TextView countryView, TextView latitudeView, TextView longitudeView,
                        int scheduleType, CrUiSchedule crUiSchedule,
                        CheckBox privacyView) {
        this(titleView.getText().toString(), subjectView.getText().toString(),
                new CrUiLocation(locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView),
                scheduleType, crUiSchedule,
                privacyView.isChecked());
    }

    public void updateViews(EditText titleView, EditText subjectView,
                            EditText locationView, EditText addressView, EditText zipView, EditText cityView, EditText countryView, EditText latitudeView, EditText longitudeView,
                            TextView scheduleView, CheckBox privacyView) {
        titleView.setText(name);
        subjectView.setText(subject);
        privacyView.setChecked(isPrivate);
        crUiLocation.updateViews(locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView);
        if (crUiSchedule != null)
            crUiSchedule.updateViews(scheduleView);
    }

    public int getScheduleType(Context ctx) {
        String[] scheduleTypes = ctx.getResources().getStringArray(R.array.new_tertulia_dialog_schedules);
        for (int i = 0; i< scheduleTypes.length; i++)
            if (scheduleTypes[i].equals(scheduleType))
                return i;
        return -1;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrUiTertulia other = (CrUiTertulia) obj;
        return obj instanceof CrUiTertulia &&
                other.name.equals(name) &&
                other.subject.equals(subject) &&
                other.isPrivate == isPrivate &&
                other.crUiLocation.equals(crUiLocation) &&
                other.scheduleType == scheduleType &&
                other.crUiSchedule.equals(crUiSchedule);
    }

    // region Parcelable

    protected CrUiTertulia(Parcel in) {
        name = in.readString();
        subject = in.readString();
        isPrivate = in.readByte() != 0;
        crUiLocation = in.readParcelable(CrUiLocation.class.getClassLoader());
        scheduleType = in.readInt();
        crUiSchedule = in.readParcelable(CrUiSchedule.class.getClassLoader());
    }

    public static final Creator<CrUiTertulia> CREATOR = new Creator<CrUiTertulia>() {
        @Override
        public CrUiTertulia createFromParcel(Parcel in) {
            return new CrUiTertulia(in);
        }

        @Override
        public CrUiTertulia[] newArray(int size) {
            return new CrUiTertulia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(subject);
        out.writeByte((byte) (isPrivate ? 1 : 0));
        out.writeParcelable(crUiLocation, flags);
        out.writeInt(scheduleType);
        out.writeParcelable(crUiSchedule, flags);
    }

    // endregion

}
