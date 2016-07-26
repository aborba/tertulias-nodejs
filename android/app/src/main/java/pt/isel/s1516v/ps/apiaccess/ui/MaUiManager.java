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

public class MaUiManager extends UiManager {
    public final DrawerManager drawer;
    public final RecyclerView recyclerView;
    private final TextView emptyView;
    private final ProgressBar progressBar;
    private View rootView;

    public MaUiManager(Context ctx, DrawerManager drawer,
                       RecyclerView recyclerView, TertuliasArrayRvAdapter viewAdapter, TextView emptyView, ProgressBar progressBar) {
        super(ctx);
        this.drawer = drawer;
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.progressBar = progressBar;
        swapAdapter(viewAdapter);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
    }

    public MaUiManager(Context ctx, DrawerLayout drawerLayout, ListView drawerMenuView, ImageView drawerUserImage,
                       RecyclerView recyclerView, TertuliasArrayRvAdapter viewAdapter, TextView emptyView, ProgressBar progressBar) {
        this(ctx, new DrawerManager(ctx, drawerLayout, drawerMenuView, drawerUserImage), recyclerView, viewAdapter, emptyView, progressBar);
    }

    public MaUiManager(Context ctx, DrawerManager drawer,
                       int recyclerView, TertuliasArrayRvAdapter viewAdapter, int emptyView, int progressBar) {
        this(ctx, drawer,
                (RecyclerView) ((Activity) ctx).findViewById(recyclerView),
                viewAdapter,
                (TextView) ((Activity) ctx).findViewById(emptyView),
                (ProgressBar) ((Activity) ctx).findViewById(progressBar));
    }

    public MaUiManager(Context ctx, int rDrawerLayout, int rDrawerMenuView, int rDrawerUserImage,
                       int rRecyclerView, TertuliasArrayRvAdapter viewAdapter, int rEmptyView, int rProgressBar) {
        this(ctx, new DrawerManager(ctx, (DrawerLayout) ((Activity) ctx).findViewById(rDrawerLayout),
                        (ListView) ((Activity) ctx).findViewById(rDrawerMenuView),
                        (ImageView) ((Activity) ctx).findViewById(rDrawerUserImage)),
                (RecyclerView) ((Activity) ctx).findViewById(rRecyclerView),
                viewAdapter,
                (TextView) ((Activity) ctx).findViewById(rEmptyView),
                (ProgressBar) ((Activity) ctx).findViewById(rProgressBar));
    }

    public MaUiManager swapAdapter(TertuliasArrayRvAdapter viewAdapter) {
        Util.setupAdapter((Activity) ctx, recyclerView, viewAdapter);
        return this;
    }

    // region UiManager

    @Override
    public boolean isGeoData() {
        return false;
    }

    @Override
    public boolean isGeo() {
        return false;
    }

    @Override
    public String getLatitudeData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLongitudeData() {
        throw new UnsupportedOperationException();
    }

    // endregion

    // region EmptyView

    public MaUiManager setEmpty(boolean isEmpty) {
        if (isEmpty)
            setEmpty();
        else
            resetEmpty();
        return this;
    }

    public MaUiManager setEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        return this;
    }

    public MaUiManager resetEmpty() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        return this;
    }

    // endregion

    // region ProgressBar

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    // endregion
}
