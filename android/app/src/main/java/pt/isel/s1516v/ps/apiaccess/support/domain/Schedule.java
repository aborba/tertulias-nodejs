package pt.isel.s1516v.ps.apiaccess.support.domain;

import java.util.Date;

public interface Schedule {

    Date nextEvent();

    String toString();

}
