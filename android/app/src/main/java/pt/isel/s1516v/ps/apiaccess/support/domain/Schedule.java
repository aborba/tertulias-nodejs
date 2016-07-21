package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcelable;

import java.util.Date;

public interface Schedule {

    Date nextEvent();

    String toString();

}
