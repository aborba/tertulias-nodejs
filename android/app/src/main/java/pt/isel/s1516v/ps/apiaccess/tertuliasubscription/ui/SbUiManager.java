/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.tertuliasubscription.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyD;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class SbUiManager extends UiManager {

    public enum UIRESOURCE {
        TITLE, SUBJECT,
        LOCATION, ADDRESS, ZIP, CITY, COUNTRY, LATITUDE, LONGITUDE,
        SCHEDULE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources;
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
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
        if (tertulia instanceof TertuliaEditionWeekly || tertulia instanceof TertuliaEditionMonthlyD) {
            scheduleText = tertulia.toString();
        } else {
            if (tertulia.scheduleType != null) {
                scheduleText = tertulia.scheduleType.toString();
                switch (tertulia.scheduleType.name()) {
                    case "WEEKLY":
                        scheduleText += " - " + tertulia.toString();
                        break;
                    case "MONTHLYD":
                        scheduleText += " - " + tertulia.toString();
                        break;
                    case "MONTHLYW":
                        scheduleText += " - " + tertulia.toString();
                        break;
                    case "YEARLY":
                    case "YEARLYW":
//                        scheduleText += " - " + ((TertuliaEditionYearlyW) tertulia).toString();
                        throw new UnsupportedOperationException();
                    default:
                        throw new RuntimeException();
                }
            } else {
                scheduleText = "";
            }
        }
        scheduleView.setText(scheduleText);
    }

    // endregion

}