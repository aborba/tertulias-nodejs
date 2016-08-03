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

package pt.isel.s1516v.ps.apiaccess.tertuliadetails.ui;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class DtUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        TITLE, SUBJECT,
        ROLE,
        LOCATION, ADDRESS, ZIP, CITY, COUNTRY, LATITUDE, LONGITUDE,
        SCHEDULE,
        PRIVACY
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    private Toolbar toolbarView;
    private TextView titleView, subjectView, roleView,
            locationView, addressView, zipView, cityView, countryView,
            latitudeView, longitudeView,
            scheduleView;
    private CheckBox isPrivateView;

    public DtUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.toolbar);
        uiResources.put(UIRESOURCE.TITLE, R.id.tda_title);
        uiResources.put(UIRESOURCE.SUBJECT, R.id.tda_subject);
        uiResources.put(UIRESOURCE.ROLE, R.id.tda_role);
        uiResources.put(UIRESOURCE.LOCATION, R.id.tda_locationName);
        uiResources.put(UIRESOURCE.ADDRESS, R.id.tda_address);
        uiResources.put(UIRESOURCE.ZIP, R.id.tda_zip);
        uiResources.put(UIRESOURCE.CITY, R.id.tda_city);
        uiResources.put(UIRESOURCE.COUNTRY, R.id.tda_country);
        uiResources.put(UIRESOURCE.LATITUDE, R.id.tda_latitude);
        uiResources.put(UIRESOURCE.LONGITUDE, R.id.tda_longitude);
        uiResources.put(UIRESOURCE.SCHEDULE, R.id.tda_schedule);
        uiResources.put(UIRESOURCE.PRIVACY, R.id.tda_isPrivate);
    }

    public void set(TertuliaEdition tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
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
        toolbarView = setup(UIRESOURCE.TOOLBAR, Toolbar.class, uiViews);
        titleView = setup(UIRESOURCE.TITLE, TextView.class, uiViews);
        subjectView = setup(UIRESOURCE.SUBJECT, TextView.class, uiViews);
        roleView = setup(UIRESOURCE.ROLE, TextView.class, uiViews);
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
        isPrivateView.setChecked(tertulia.isPrivate);
        roleView.setText(tertulia.role.name);
        locationView.setText(tertulia.location.name);
        addressView.setText(tertulia.location.address.address);
        zipView.setText(tertulia.location.address.zip);
        cityView.setText(tertulia.location.address.city);
        countryView.setText(tertulia.location.address.country);
        latitudeView.setText(tertulia.location.geolocation.getLatitude());
        longitudeView.setText(tertulia.location.geolocation.getLongitude());
        if (tertulia.tertuliaSchedule != null)
            scheduleView.setText(tertulia.tertuliaSchedule.toString());
    }

    // endregion

}