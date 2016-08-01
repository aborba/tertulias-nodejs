package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.common.base.Function;

import java.util.EnumMap;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.support.domain.Address;
import pt.isel.s1516v.ps.apiaccess.support.domain.Geolocation;
import pt.isel.s1516v.ps.apiaccess.support.domain.LocationCreation;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreation;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreationWeekly;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class CrUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        TERTULIA_NAME, SUBJECT,
        LOCATION_NAME, ADDRESS, ZIP, CITY, COUNTRY, LATITUDE, LONGITUDE,
        SCHEDULE_DESCRIPTION,
        ISPRIVATE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);;
    private boolean isViewsSet;

    private EditText tertuliaNameView, subjectView,
            locationNameView, addressView, zipView, cityView, countryView,
            latitudeView, longitudeView;
    private TextView scheduleDescriptionView;
    private CheckBox isPrivateView;

    public CrUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.toolbar);
        uiResources.put(UIRESOURCE.TERTULIA_NAME, R.id.tertuliaName);
        uiResources.put(UIRESOURCE.SUBJECT, R.id.subject);
        uiResources.put(UIRESOURCE.LOCATION_NAME, R.id.locationName);
        uiResources.put(UIRESOURCE.ADDRESS, R.id.address);
        uiResources.put(UIRESOURCE.ZIP, R.id.zip);
        uiResources.put(UIRESOURCE.CITY, R.id.city);
        uiResources.put(UIRESOURCE.COUNTRY, R.id.country);
        uiResources.put(UIRESOURCE.LATITUDE, R.id.latitude);
        uiResources.put(UIRESOURCE.LONGITUDE, R.id.longitude);
        uiResources.put(UIRESOURCE.SCHEDULE_DESCRIPTION, R.id.scheduleDescription);
        uiResources.put(UIRESOURCE.ISPRIVATE, R.id.isPrivate);
    }

    public void set(TertuliaCreation tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
    }

    public void set(Place place) {
        set(place, new MyFormatter("%.6f"));
    }

    public void set(Place place, Function<Double, String> formatter) {
        if (place == null)
            return;
        lazyViewsSetup();
        locationNameView.setText(place.getName());
        CrUiAddress crUiAddress = new CrUiAddress(place.getAddress().toString());
        addressView.setText(crUiAddress.address);
        zipView.setText(crUiAddress.zip);
        cityView.setText(crUiAddress.city);
        countryView.setText(crUiAddress.country);
        latitudeView.setText(formatter.apply(place.getLatLng().latitude));
        longitudeView.setText(formatter.apply(place.getLatLng().longitude));
    }

    public TertuliaCreation update(TertuliaCreation tertulia) {
        Address address = new Address(addressView.getText().toString(), zipView.getText().toString(),
                cityView.getText().toString(), countryView.getText().toString());
        Geolocation geolocation = new Geolocation(getLatitudeData(), getLongitudeData());
        LocationCreation location = new LocationCreation(locationNameView.getText().toString(), address, geolocation);
        TertuliaCreation newTertulia = new TertuliaCreation(tertuliaNameView.getText().toString(),
                subjectView.getText().toString(), isPrivateView.isChecked(), location, tertulia.scheduleType, tertulia.getSchedule());
        return newTertulia;
    }

    public View getView(UIRESOURCE uiresource) {
        lazyViewsSetup();
        return uiViews.get(uiresource);
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

    // endregion

    // region public static methods

    public static EnumMap<UIRESOURCE, Integer> getDictionary() {
        return new EnumMap<>(UIRESOURCE.class);
    }

    @Override
    protected int getUiResource(String resource) {
        return uiResources.get(UIRESOURCE.valueOf(resource));
    }

    // endregion

    // region private methods

    private void lazyViewsSetup() {
        if (isViewsSet)
            return;
        tertuliaNameView = setup(UIRESOURCE.TERTULIA_NAME, EditText.class, uiViews);
        subjectView = setup(UIRESOURCE.SUBJECT, EditText.class, uiViews);
        locationNameView = setup(UIRESOURCE.LOCATION_NAME, EditText.class, uiViews);
        addressView = setup(UIRESOURCE.ADDRESS, EditText.class, uiViews);
        zipView = setup(UIRESOURCE.ZIP, EditText.class, uiViews);
        cityView = setup(UIRESOURCE.CITY, EditText.class, uiViews);
        countryView = setup(UIRESOURCE.COUNTRY, EditText.class, uiViews);
        latitudeView = setup(UIRESOURCE.LATITUDE, EditText.class, uiViews);
        longitudeView = setup(UIRESOURCE.LONGITUDE, EditText.class, uiViews);
        scheduleDescriptionView = setup(UIRESOURCE.SCHEDULE_DESCRIPTION, EditText.class, uiViews);
        isPrivateView = setup(UIRESOURCE.ISPRIVATE, CheckBox.class, uiViews);
        isViewsSet = true;
    }

    private void fillInViews(TertuliaCreation tertulia) {
        tertuliaNameView.setText(tertulia.name);
        subjectView.setText(tertulia.subject);
        locationNameView.setText(tertulia.location.name);
        addressView.setText(tertulia.location.address.address);
        zipView.setText(tertulia.location.address.zip);
        cityView.setText(tertulia.location.address.city);
        countryView.setText(tertulia.location.address.country);
        latitudeView.setText(tertulia.location.geolocation.getLatitude());
        longitudeView.setText(tertulia.location.geolocation.getLongitude());
        String scheduleText;
        if (tertulia instanceof TertuliaCreationWeekly
//                || tertulia instanceof TertuliaCreationMonthly || tertulia instanceof TertuliaCreationMonthlyW
//                || tertulia instanceof EditCreationYearly || tertulia instanceof EditCreationYearlyW
        )
            scheduleText = tertulia.toString();
        else {
            if (tertulia.scheduleType != null) {
                scheduleText = tertulia.scheduleType.toString();
                switch (tertulia.scheduleType.name()) {
                    case "WEEKLY":
                        scheduleText += " - " + ((TertuliaCreationWeekly) tertulia).toString();
                        break;
                    case "MONTHLYD":
//                        scheduleText += " - " + ((TertuliaCreationMonthly) tertulia).toString();
                        break;
                    case "MONTHLYW":
//                        scheduleText += " - " + ((TertuliaCreationMonthlyW) tertulia).toString();
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
        scheduleDescriptionView.setText(scheduleText);
        isPrivateView.setChecked(tertulia.isPrivate);
    }

    // endregion

    // region Private Classes

    private class MyFormatter implements Function<Double, String> {
        String format;

        public MyFormatter(String format) {
            this.format = format;
        }

        @Override
        public String apply(Double value) {
            return String.format(Locale.getDefault(), format, value);
        }
    }

    // endregion

}