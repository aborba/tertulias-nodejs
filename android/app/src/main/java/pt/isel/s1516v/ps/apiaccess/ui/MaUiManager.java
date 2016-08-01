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

package pt.isel.s1516v.ps.apiaccess.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasArrayAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class MaUiManager extends UiManager {

    private DrawerManager drawerManager;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ProgressBar progressBar;

    public enum UIRESOURCE {
        DRAWER_LAYOUT,
        DRAWER_MENU_LIST,
        USER_PICTURE,
        RECYCLER_VIEW,
        EMPTY_VIEW,
        PROGRESSBAR
    }

    public final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    public MaUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.DRAWER_LAYOUT, R.id.drawer_layout);
        uiResources.put(UIRESOURCE.DRAWER_MENU_LIST, R.id.menu_list);
        uiResources.put(UIRESOURCE.USER_PICTURE, R.id.mtl_user_picture);
        uiResources.put(UIRESOURCE.RECYCLER_VIEW, R.id.mtl_RecyclerView);
        uiResources.put(UIRESOURCE.EMPTY_VIEW, R.id.mtl_empty_view);
        uiResources.put(UIRESOURCE.PROGRESSBAR, R.id.mtl_progressbar);
    }

    public void setDrawerManager(DrawerManager drawerManager) {
        this.drawerManager = drawerManager;
    }

    public DrawerManager getDrawerManager() {
        return drawerManager;
    }

    public void setUserPicture(int resource) {
        drawerManager.setIcon(resource);
    }

    public void setUserPicture(String resource) {
        drawerManager.setIcon(resource);
    }

    public MaUiManager swapAdapter(TertuliasArrayAdapter viewAdapter) {
        lazyViewsSetup();
        Util.setupAdapter((Activity) ctx, recyclerView, viewAdapter);
        return this;
    }

    public View getView(UIRESOURCE uiresource) {
        lazyViewsSetup();
        return uiViews.get(uiresource);
    }

    // region UiManager

    @Override
    public boolean isGeoCapability() {
        return false;
    }

    @Override
    public boolean isGeo() {
        return false;
    }

    @Override
    public boolean isLatitude() {
        return false;
    }

    @Override
    public String getLatitudeData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLongitude() {
        return false;
    }

    @Override
    public String getLongitudeData() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getUiResource(String resource) {
        return uiResources.get(UIRESOURCE.valueOf(resource));
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
        lazyViewsSetup();
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        return this;
    }

    public MaUiManager resetEmpty() {
        lazyViewsSetup();
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        return this;
    }

    // endregion

    // region ProgressBar

    public void showProgressBar() {
        lazyViewsSetup();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        lazyViewsSetup();
        progressBar.setVisibility(View.INVISIBLE);
    }

    // endregion

    // region private methods

    private void lazyViewsSetup() {
        if (isViewsSet)
            return;
        recyclerView = setup(UIRESOURCE.RECYCLER_VIEW, RecyclerView.class, uiViews);
        emptyView = setup(UIRESOURCE.EMPTY_VIEW, TextView.class, uiViews);
        progressBar = setup(UIRESOURCE.PROGRESSBAR, ProgressBar.class, uiViews);
        isViewsSet = true;
    }

//    private View findViewById(int resource) {
//        return ((Activity) ctx).findViewById(resource);
//    }
//
//    private View findView(UIRESOURCE resource) {
//        return findViewById(uiResources.get(resource));
//    }
//
//    private <T extends View> T setup(Class<T> viewType, UIRESOURCE resource) {
//        T view = (T) findView(resource);
//        uiViews.put(resource, view);
//        return view;
//    }

    // endregion

}
