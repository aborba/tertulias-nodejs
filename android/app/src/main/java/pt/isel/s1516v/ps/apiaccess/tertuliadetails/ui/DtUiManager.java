package pt.isel.s1516v.ps.apiaccess.tertuliadetails.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import pt.isel.s1516v.ps.apiaccess.TertuliasArrayRvAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadTertulia;
import pt.isel.s1516v.ps.apiaccess.ui.DrawerManager;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class DtUiManager extends UiManager {

    public final TextView titleView, subjectView, roleView;
    public final TextView locationView, addressView, zipView, cityView, countryView;
    public final TextView latitudeView, longitudeView;
    public final TextView scheduleView;
    public final CheckBox isPrivateView;

    public DtUiManager(Context ctx,
                       TextView titleView, TextView subjectView,
                       TextView roleView,
                       TextView locationView, TextView addressView, TextView zipView, TextView cityView, TextView countryView,
                       TextView latitudeView, TextView longitudeView,
                       TextView scheduleView,
                       CheckBox isPrivateView) {
        super(ctx);
        this.titleView = titleView;
        this.subjectView = subjectView;
        this.roleView = roleView;
        this.locationView = locationView;
        this.addressView = addressView;
        this.zipView = zipView;
        this.cityView = cityView;
        this.countryView = countryView;
        this.latitudeView = latitudeView;
        this.longitudeView = longitudeView;
        this.scheduleView = scheduleView;
        this.isPrivateView = isPrivateView;
    }

    public DtUiManager(Context ctx,
                       int titleView, int subjectView,
                       int roleView,
                       int locationView, int addressView, int zipView, int cityView, int countryView,
                       int latitudeView, int longitudeView,
                       int scheduleView,
                       int isPrivateView) {
        this(ctx,
                (TextView) ((Activity) ctx).findViewById(titleView),
                (TextView) ((Activity) ctx).findViewById(subjectView),
                (TextView) ((Activity) ctx).findViewById(roleView),
                (TextView) ((Activity) ctx).findViewById(locationView),
                (TextView) ((Activity) ctx).findViewById(addressView),
                (TextView) ((Activity) ctx).findViewById(zipView),
                (TextView) ((Activity) ctx).findViewById(cityView),
                (TextView) ((Activity) ctx).findViewById(countryView),
                (TextView) ((Activity) ctx).findViewById(latitudeView),
                (TextView) ((Activity) ctx).findViewById(longitudeView),
                (TextView) ((Activity) ctx).findViewById(scheduleView),
                (CheckBox) ((Activity) ctx).findViewById(isPrivateView));
    }

    private String getValue(TextView view) {
        return view.getText().toString();
    }

    private boolean isValue(TextView view) {
        return !TextUtils.isEmpty(getValue(view));
    }

    public boolean isLatitude() {
        return isValue(latitudeView);
    }

    public boolean isLongitude() {
        return isValue(longitudeView);
    }

    public void present(ReadTertulia tertulia) {
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
        if (!TextUtils.isEmpty(tertulia.scheduleType)) {
            scheduleText = tertulia.scheduleType;
            if (!TextUtils.isEmpty(tertulia.scheduleDescription))
                scheduleText += " - " + tertulia.scheduleDescription;
        } else scheduleText = tertulia.scheduleDescription;
        scheduleView.setText(scheduleText);
        roleView.setText(tertulia.role_type);
        isPrivateView.setChecked(tertulia.isPrivate);
    }

    // region UiManager

    @Override
    public boolean isGeoData() {
        return true;
    }

    @Override
    public boolean isGeo() {
        return isLatitude() && isLongitude();
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

}