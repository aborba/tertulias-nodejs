package pt.isel.s1516v.ps.apiaccess.tertuliasubscription.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthly;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class SbUiManager extends UiManager {

    public enum UIRESOURCE {
        TITLE, SUBJECT,
        LOCATION, ADDRESS, ZIP, CITY, COUNTRY, LATITUDE, LONGITUDE,
        SCHEDULE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources;
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);;
    private boolean isViewsSet;

    private TextView titleView, subjectView,
            locationView, addressView, zipView, cityView, countryView,
            latitudeView, longitudeView,
            scheduleView;

    public SbUiManager(Context ctx, EnumMap<UIRESOURCE, Integer> uiResources) {
        super(ctx);
        this.uiResources = uiResources;
    }

    public void set(TertuliaEdition tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
    }

    public String getTextViewValue(UIRESOURCE uiresource) {
        View view = uiViews.get(uiresource);
        if (view instanceof TextView)
            return ((TextView) view).getText().toString();
        throw new RuntimeException();
    }

    public boolean isCheckBoxChecked(UIRESOURCE uiresource) {
        View view = uiViews.get(uiresource);
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
        titleView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.TITLE));
        uiViews.put(UIRESOURCE.TITLE, titleView);
        subjectView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.SUBJECT));
        uiViews.put(UIRESOURCE.SUBJECT, subjectView);
        locationView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.LOCATION));
        uiViews.put(UIRESOURCE.LOCATION, locationView);
        addressView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.ADDRESS));
        uiViews.put(UIRESOURCE.ADDRESS, addressView);
        zipView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.ZIP));
        uiViews.put(UIRESOURCE.ZIP, zipView);
        cityView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.CITY));
        uiViews.put(UIRESOURCE.CITY, cityView);
        countryView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.COUNTRY));
        uiViews.put(UIRESOURCE.COUNTRY, countryView);
        latitudeView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.LATITUDE));
        uiViews.put(UIRESOURCE.LATITUDE, latitudeView);
        longitudeView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.LONGITUDE));
        uiViews.put(UIRESOURCE.LONGITUDE, longitudeView);
        scheduleView = (TextView) ((Activity) ctx).findViewById(uiResources.get(UIRESOURCE.SCHEDULE));
        uiViews.put(UIRESOURCE.SCHEDULE, scheduleView);
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
        if (tertulia instanceof TertuliaEditionWeekly || tertulia instanceof TertuliaEditionMonthly) {
            scheduleText = tertulia.toString();
        } else {
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
//                        scheduleText += " - " + ((TertuliaEditionYearlyW) tertulia).toString();
                        throw new UnsupportedOperationException();
                    default:
                        throw new RuntimeException();
                }
            } else scheduleText = tertulia.scheduleType.toString();
        }
        scheduleView.setText(scheduleText);
    }

    // endregion

}