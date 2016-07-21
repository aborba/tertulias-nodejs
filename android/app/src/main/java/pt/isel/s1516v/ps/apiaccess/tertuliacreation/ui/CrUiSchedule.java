package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.os.Parcelable;
import android.widget.TextView;

import java.util.Date;

public interface CrUiSchedule extends Parcelable {

    void updateViews(TextView scheduleView);

    Date nextEvent();

    String toString();

}
