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

public class CrUiWeekly implements Parcelable, CrUiSchedule {

    public int weekDayNr;
    public int skip;

    private String[] days, weeks;

    public CrUiWeekly(int weekDayNr, int skip) {
        this.weekDayNr = weekDayNr;
        this.skip = skip;
    }

    public String getWeekDay(Context ctx) {
        if (days == null)
            days = ctx.getResources().getStringArray(R.array.new_monthlyw_weekday);
        return days[weekDayNr];
    }

    public String getWeeks(Context ctx) {
        if (weeks == null)
            weeks = ctx.getResources().getStringArray(R.array.new_weekly_skip);
        return weeks[skip];
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_weekly_tostring);
        String result = parts[0];
        if (skip == 0)
            result += parts[1];
        else
            result += String.format(Locale.getDefault(), parts[2], getWeeks(ctx));
        result += String.format(parts[3],
                getWeekDay(ctx));
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

    protected CrUiWeekly(Parcel in) {
        weekDayNr = in.readInt();
        skip = in.readInt();
    }

    public static final Creator<CrUiWeekly> CREATOR = new Creator<CrUiWeekly>() {
        @Override
        public CrUiWeekly createFromParcel(Parcel in) {
            return new CrUiWeekly(in);
        }

        @Override
        public CrUiWeekly[] newArray(int size) {
            return new CrUiWeekly[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(weekDayNr);
        dest.writeInt(skip);
    }

    // endregion
}
