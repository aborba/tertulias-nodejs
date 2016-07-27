package pt.isel.s1516v.ps.apiaccess.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import pt.isel.s1516v.ps.apiaccess.TertuliasArrayRvAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public abstract class UiManager {
    final protected Context ctx;
    private View rootView;

    public UiManager(Context ctx) {
        this.ctx = ctx;
    }

    public View getRootView() {
        if (rootView == null)
            rootView = ((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content);
        return rootView;
    }

    public abstract boolean isGeoCapability();

    public abstract boolean isGeo();

    public abstract boolean isLatitude();

    public abstract String getLatitudeData();

    public abstract boolean isLongitude();

    public abstract String getLongitudeData();

}
