package pt.isel.s1516v.ps.apiaccess.tertuliaedition.ui;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.Address;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthly;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.support.domain.Geolocation;
import pt.isel.s1516v.ps.apiaccess.support.domain.LocationEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class EdUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        TITLE, SUBJECT,
        LOCATION, ADDRESS, ZIP, CITY, COUNTRY, LATITUDE, LONGITUDE,
        SCHEDULE,
        PRIVACY
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);;
    private boolean isViewsSet;

    private Toolbar toolbar;
    private TextView titleView, subjectView,
            locationView, addressView, zipView, cityView, countryView,
            latitudeView, longitudeView,
            scheduleView;
    private CheckBox isPrivateView;

    public EdUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.toolbar);
        uiResources.put(UIRESOURCE.TITLE, R.id.tertuliaName);
        uiResources.put(UIRESOURCE.SUBJECT, R.id.subject);
        uiResources.put(UIRESOURCE.LOCATION, R.id.locationName);
        uiResources.put(UIRESOURCE.ADDRESS, R.id.address);
        uiResources.put(UIRESOURCE.ZIP, R.id.zip);
        uiResources.put(UIRESOURCE.CITY, R.id.city);
        uiResources.put(UIRESOURCE.COUNTRY, R.id.country);
        uiResources.put(UIRESOURCE.LATITUDE, R.id.latitude);
        uiResources.put(UIRESOURCE.LONGITUDE, R.id.longitude);
        uiResources.put(UIRESOURCE.SCHEDULE, R.id.scheduleDescription);
        uiResources.put(UIRESOURCE.PRIVACY, R.id.isPrivate);
    }

    public void set(TertuliaEdition tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
    }

    public TertuliaEdition update(TertuliaEdition tertulia) {
        Address address = new Address(
                getTextViewValue(UIRESOURCE.ADDRESS),
                getTextViewValue(UIRESOURCE.ZIP),
                getTextViewValue(UIRESOURCE.CITY),
                getTextViewValue(UIRESOURCE.COUNTRY));
        Geolocation geolocation = new Geolocation(
                Util.string2Double(getTextViewValue(UIRESOURCE.LATITUDE)),
                Util.string2Double(getTextViewValue(UIRESOURCE.LONGITUDE)));
        LocationEdition location = new LocationEdition(
                tertulia.location.id,
                getTextViewValue(UIRESOURCE.LOCATION),
                address, geolocation);
        int scheduleId;
        String scheduleDescription;
        switch (tertulia.scheduleType.name()) {
            case "WEEKLY":
                if (! (tertulia instanceof TertuliaEditionWeekly))
                    throw new RuntimeException();
                TertuliaEditionWeekly tertuliaEditionWeekly = (TertuliaEditionWeekly)tertulia;
                scheduleId = tertuliaEditionWeekly.schedule_id;
                scheduleDescription = tertuliaEditionWeekly.toString();
                break;
            case "MONTHLYD":
                if (! (tertulia instanceof TertuliaEditionMonthly))
                    throw new RuntimeException();
                TertuliaEditionMonthly tertuliaEditionMonthly = (TertuliaEditionMonthly)tertulia;
                scheduleId = tertuliaEditionMonthly.schedule_id;
                scheduleDescription = tertuliaEditionMonthly.toString();
                break;
            case "MONTHLYW":
                if (! (tertulia instanceof TertuliaEditionMonthlyW))
                    throw new RuntimeException();
                TertuliaEditionMonthlyW tertuliaEditionMonthlyW = (TertuliaEditionMonthlyW)tertulia;
                scheduleId = tertuliaEditionMonthlyW.schedule_id;
                scheduleDescription = tertuliaEditionMonthlyW.toString();
                break;
            case "YEARLY":
            case "YEARLYW":
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException();
        }
        ApiTertuliaEdition apiTertuliaEdition = new ApiTertuliaEdition(
                String.valueOf(tertulia.id), getTextViewValue(UIRESOURCE.TITLE), getTextViewValue(UIRESOURCE.SUBJECT), isCheckBoxChecked(UIRESOURCE.PRIVACY),
                String.valueOf(tertulia.location.id), getTextViewValue(UIRESOURCE.LOCATION), getTextViewValue(UIRESOURCE.ADDRESS), getTextViewValue(UIRESOURCE.ZIP), getTextViewValue(UIRESOURCE.CITY), getTextViewValue(UIRESOURCE.COUNTRY),
                getTextViewValue(UIRESOURCE.LATITUDE), getTextViewValue(UIRESOURCE.LONGITUDE),
                String.valueOf(scheduleId), tertulia.scheduleType.toString(), scheduleDescription,
                "1", "owner", 0);
        switch (tertulia.scheduleType.name()) { // TODO: Acho que não é preciso
            case "WEEKLY":
                TertuliaEditionWeekly etw = (TertuliaEditionWeekly)tertulia;
                return new TertuliaEditionWeekly(apiTertuliaEdition, tertulia.links, etw.schedule_id, etw.weekday, etw.skip);
            case "MONTHLYD":
                TertuliaEditionMonthly etm = (TertuliaEditionMonthly)tertulia;
                return new TertuliaEditionMonthly(apiTertuliaEdition, tertulia.links, etm.schedule_id, etm.dayNr, etm.isFromStart, etm.skip);
            case "MONTHLYW":
                TertuliaEditionMonthlyW etmw = (TertuliaEditionMonthlyW)tertulia;
                return new TertuliaEditionMonthlyW(apiTertuliaEdition, tertulia.links, etmw.schedule_id, etmw.weekday, etmw.weeknr, etmw.isFromStart, etmw.skip);
            case "YEARLY":
            case "YEARLYW":
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException();
        }
    }

    public View getView(UIRESOURCE uiresource) {
        lazyViewsSetup();
        return uiViews.get(uiresource);
    }

    public String getTextViewValue(UIRESOURCE uiresource) {
        View view = getView(uiresource);
        if (view instanceof TextView)
            return ((TextView) view).getText().toString();
        throw new RuntimeException();
    }

    public void setTextViewValue(UIRESOURCE uiresource, String value) {
        View view = getView(uiresource);
        if (!(view instanceof TextView))
            throw new RuntimeException();
        ((TextView) view).setText(value);
    }

    public boolean isCheckBoxChecked(UIRESOURCE uiresource) {
        View view = getView(uiresource);
        if (view instanceof CheckBox)
            return ((CheckBox) view).isChecked();
        throw new RuntimeException();
    }

    private String getValue(TextView view) {
        return view.getText().toString();
    }

    private boolean getValue(CheckBox view) {
        return view.isChecked();
    }

    private boolean isValue(TextView view) {
        return !TextUtils.isEmpty(getValue(view));
    }

    // region UiManager

    @Override
    public boolean isGeoCapability() {
        return true;
    }

    @Override
    public boolean isGeo() {
        return isLatitude() && isLongitude();
    }

    @Override
    public boolean isLatitude() {
        return isValue(latitudeView);
    }

    @Override
    public boolean isLongitude() {
        return isValue(longitudeView);
    }

    @Override
    public String getLatitudeData() {
        return latitudeView.getText().toString();
    }

    @Override
    public String getLongitudeData() {
        return longitudeView.getText().toString();
    }

    @Override
    protected int getUiResource(String resource) {
        return uiResources.get(UIRESOURCE.valueOf(resource));
    }

    // endregion

    // region public static methods

    public static EnumMap<UIRESOURCE, Integer> getDictionary() {
        return new EnumMap<>(UIRESOURCE.class);
    }

    // endregion

    // region private methods

    private void lazyViewsSetup() {
        if (isViewsSet)
            return;
        toolbar = setup(UIRESOURCE.TOOLBAR, Toolbar.class, uiViews);
        titleView = setup(UIRESOURCE.TITLE, TextView.class, uiViews);
        subjectView = setup(UIRESOURCE.SUBJECT, TextView.class, uiViews);
        locationView = setup(UIRESOURCE.LOCATION, TextView.class, uiViews);
        addressView = setup(UIRESOURCE.ADDRESS, TextView.class, uiViews);
        zipView = setup(UIRESOURCE.ZIP, TextView.class, uiViews);
        cityView = setup(UIRESOURCE.CITY, TextView.class, uiViews);
        countryView = setup(UIRESOURCE.COUNTRY, TextView.class, uiViews);
        latitudeView = setup(UIRESOURCE.LATITUDE, TextView.class, uiViews);
        longitudeView = setup(UIRESOURCE.LONGITUDE, TextView.class, uiViews);
        scheduleView = setup(UIRESOURCE.SCHEDULE, TextView.class, uiViews);
        isPrivateView = setup(UIRESOURCE.PRIVACY, CheckBox.class, uiViews);
        isViewsSet = true;
    }

    private void fillInViews(TertuliaEdition tertulia) {
        titleView.setText(tertulia.name);
        subjectView.setText(tertulia.subject);
        locationView.setText(tertulia.location.name);
        addressView.setText(tertulia.location.address.address);
        zipView.setText(tertulia.location.address.zip);
        cityView.setText(tertulia.location.address.city);
        countryView.setText(tertulia.location.address.country);
        latitudeView.setText(tertulia.location.geolocation.getLatitude());
        longitudeView.setText(tertulia.location.geolocation.getLongitude());
        String scheduleText;
        if (tertulia instanceof TertuliaEditionWeekly || tertulia instanceof TertuliaEditionMonthly || tertulia instanceof TertuliaEditionMonthlyW)
            scheduleText = tertulia.toString();
        else {
            if (tertulia.scheduleType != null) {
                scheduleText = tertulia.scheduleType.toString();
                switch (tertulia.scheduleType.name()) {
                    case "WEEKLY":
                        scheduleText += " - " + ((TertuliaEditionWeekly) tertulia).toString();
                        break;
                    case "MONTHLYD":
                        scheduleText += " - " + ((TertuliaEditionMonthly) tertulia).toString();
                        break;
                    case "MONTHLYW":
                        scheduleText += " - " + ((TertuliaEditionMonthlyW) tertulia).toString();
                        break;
                    case "YEARLY":
                    case "YEARLYW":
//                        scheduleText += " - " + ((TertuliaEditionYearly) tertulia).toString();
                        throw new UnsupportedOperationException();
                    default:
                        throw new RuntimeException();
                }
            } else scheduleText = "";
        }
        scheduleView.setText(scheduleText);
        isPrivateView.setChecked(tertulia.isPrivate);
    }

    // endregion

}