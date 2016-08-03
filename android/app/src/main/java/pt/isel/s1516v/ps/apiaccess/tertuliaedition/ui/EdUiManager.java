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
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class EdUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        TITLE, SUBJECT, PRIVACY,
        LOCATION, ADDRESS, ZIP, CITY, COUNTRY, LATITUDE, LONGITUDE,
        SCHEDULE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    private Toolbar toolbarView;
    private TextView tertuliaNameView, subjectView,
            locationNameView, addressView, zipView, cityView, countryView,
            latitudeView, longitudeView,
            scheduleView;
    private CheckBox isPrivateView;

    public EdUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.toolbar);
        uiResources.put(UIRESOURCE.TITLE, R.id.tertuliaName);
        uiResources.put(UIRESOURCE.SUBJECT, R.id.subject);
        uiResources.put(UIRESOURCE.PRIVACY, R.id.isPrivate);
        uiResources.put(UIRESOURCE.LOCATION, R.id.locationName);
        uiResources.put(UIRESOURCE.ADDRESS, R.id.address);
        uiResources.put(UIRESOURCE.ZIP, R.id.zip);
        uiResources.put(UIRESOURCE.CITY, R.id.city);
        uiResources.put(UIRESOURCE.COUNTRY, R.id.country);
        uiResources.put(UIRESOURCE.LATITUDE, R.id.latitude);
        uiResources.put(UIRESOURCE.LONGITUDE, R.id.longitude);
        uiResources.put(UIRESOURCE.SCHEDULE, R.id.scheduleDescription);
    }

    public void set(TertuliaEdition tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
    }

    public void update(TertuliaEdition tertulia) {
        tertulia.name = tertuliaNameView.getText().toString().trim();
        tertulia.subject = subjectView.getText().toString().trim();
        tertulia.isPrivate = isPrivateView.isChecked();
        tertulia.location.address.address = addressView.getText().toString().trim();
        tertulia.location.address.zip = zipView.getText().toString().trim();
        tertulia.location.address.city = cityView.getText().toString().trim();
        tertulia.location.address.country = countryView.getText().toString().trim();
        String locationName = locationNameView.getText().toString().trim();
        if (TextUtils.isEmpty(locationName)) {
            if ( ! TextUtils.isEmpty(tertulia.location.address.address))
                locationName = tertulia.location.address.address;
            else if (!TextUtils.isEmpty(tertulia.location.address.city))
                locationName = tertulia.location.address.city;
            else if (!TextUtils.isEmpty(tertulia.location.address.country))
                locationName = tertulia.location.address.country;
            else
                locationName = tertulia.location.geolocation.toString();
        }
        tertulia.location.name = locationName;
        if (TextUtils.isEmpty(latitudeView.getText().toString()))
            tertulia.location.geolocation.isLatitude = false;
        else {
            tertulia.location.geolocation.isLatitude = true;
            tertulia.location.geolocation.latitude = Util.string2Double(latitudeView.getText().toString());
        }
        if (TextUtils.isEmpty(longitudeView.getText().toString()))
            tertulia.location.geolocation.isLongitude = false;
        else {
            tertulia.location.geolocation.isLongitude = true;
            tertulia.location.geolocation.longitude = Util.string2Double(longitudeView.getText().toString());
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
        toolbarView = setup(UIRESOURCE.TOOLBAR, Toolbar.class, uiViews);
        tertuliaNameView = setup(UIRESOURCE.TITLE, TextView.class, uiViews);
        subjectView = setup(UIRESOURCE.SUBJECT, TextView.class, uiViews);
        locationNameView = setup(UIRESOURCE.LOCATION, TextView.class, uiViews);
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
        tertuliaNameView.setText(tertulia.name);
        subjectView.setText(tertulia.subject);
        locationNameView.setText(tertulia.location.name);
        isPrivateView.setChecked(tertulia.isPrivate);
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