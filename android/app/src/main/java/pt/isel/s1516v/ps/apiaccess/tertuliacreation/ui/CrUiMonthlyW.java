package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;

public class CrUiMonthlyW implements Parcelable, CrUiSchedule {

    public int weekDayNr;
    public int weekNr, skip;
    public boolean isFromStart;

    private String[] days, weeks, months;

    public CrUiMonthlyW(int weekDayNr, int weekNr, boolean isFromStart, int skip) {
        this.weekDayNr = weekDayNr;
        this.weekNr = weekNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public String getWeekDay(Context ctx) {
        if (days == null)
            days = ctx.getResources().getStringArray(R.array.new_monthlyw_weekday);
        return days[weekDayNr];
    }

    public String getWeek(Context ctx) {
        if (weeks == null)
            weeks = ctx.getResources().getStringArray(R.array.new_monthlyw_weeknr);
        return weeks[weekNr];
    }

    public String getMonths(Context ctx) {
        if (months == null)
            months = ctx.getResources().getStringArray(R.array.new_monthly_skip);
        return months[skip];
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_monthlyw_tostring);
        String result = parts[0];
        if (skip == 0)
            result += parts[1];
        else
            result += String.format(Locale.getDefault(), parts[2], getMonths(ctx));
        result += String.format(parts[3],
                getWeekDay(ctx), getWeek(ctx));
        if (!isFromStart)
            result += parts[4];
        result += ".";
        return result;
    }

    // region CrUiSchedule

    @Override
    public void updateViews(TextView scheduleView) {
        scheduleView.setText(toString());
    }

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrUiMonthlyW(Parcel in) {
        weekDayNr = in.readInt();
        weekNr = in.readInt();
        skip = in.readInt();
        isFromStart = in.readByte() != 0;
    }

    public static final Creator<CrUiMonthlyW> CREATOR = new Creator<CrUiMonthlyW>() {
        @Override
        public CrUiMonthlyW createFromParcel(Parcel in) {
            return new CrUiMonthlyW(in);
        }

        @Override
        public CrUiMonthlyW[] newArray(int size) {
            return new CrUiMonthlyW[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(weekDayNr);
        dest.writeInt(weekNr);
        dest.writeInt(skip);
        dest.writeByte((byte) (isFromStart ? 1 : 0));
    }

    // endregion
}
