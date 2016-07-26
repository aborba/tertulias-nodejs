package pt.isel.s1516v.ps.apiaccess.tertuliadetails.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiLocation;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class DtUiTertulia extends CrUiTertulia {

    final String roleName;

    public DtUiTertulia(TextView titleView, TextView subjectView,
                        TextView roleView,
                        TextView locationView, TextView addressView, EditText zipView, TextView cityView, TextView countryView,
                        TextView latitudeView, TextView longitudeView,
                        int scheduleType, CrUiSchedule crUiSchedule,
                        CheckBox privacyView) {
        super(titleView, subjectView,
                locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView,
                scheduleType, crUiSchedule,
                privacyView);
        roleName = roleView.getText().toString();
    }

    // region Parcelable

    protected DtUiTertulia(Parcel in) {
        super(in.readString(), in.readString(),
                (CrUiLocation)in.readParcelable(CrUiLocation.class.getClassLoader()),
                in.readInt(), (CrUiSchedule)in.readParcelable(CrUiSchedule.class.getClassLoader()),
                in.readByte() != 0);
        roleName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(name);
        out.writeString(subject);
        out.writeParcelable(crUiLocation, flags);
        out.writeInt(scheduleType);
        out.writeParcelable(crUiSchedule, flags);
        out.writeByte((byte) (isPrivate ? 1 : 0));
    }

    public static final Creator<DtUiTertulia> CREATOR = new Parcelable.Creator<DtUiTertulia>() {
        @Override
        public DtUiTertulia createFromParcel(Parcel in) {
            return new DtUiTertulia(in);
        }

        @Override
        public DtUiTertulia[] newArray(int size) {
            return new DtUiTertulia[size];
        }
    };

    // endregion
}
