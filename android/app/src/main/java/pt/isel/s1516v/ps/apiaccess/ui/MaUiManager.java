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
import android.widget.ImageView;
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
    private ImageView userView, authenticatedView;
    private boolean isUserInfo;

    public enum UIRESOURCE {
        DRAWER_LAYOUT,
        DRAWER_MENU_LIST,
        USER_PICTURE,
        AUTHENTICATED_PICTURE,
        RECYCLER_VIEW,
        EMPTY_VIEW,
        PROGRESSBAR
    }

    public final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    public MaUiManager(Context ctx) {
        super(ctx);
    }

    public void setDrawerManager(DrawerManager drawerManager) {
        this.drawerManager = drawerManager;
    }

    public DrawerManager getDrawerManager() {
        return drawerManager;
    }

    public void setUserPicture(int resource) {
        drawerManager.setIcon(resource == 0 ? R.mipmap.tertulias : resource);
        isUserInfo = resource != 0;
    }

    public void setUserPicture(String resource) {
        if (resource != null) {
            drawerManager.setIcon(resource);
            isUserInfo = true;
            return;
        }
        isUserInfo = false;
        drawerManager.setIcon(R.mipmap.tertulias);
    }

    public boolean isUserInfo() {
        return isUserInfo;
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

    public Integer getResource(UIRESOURCE uiresource) {
        lazyViewsSetup();
        return uiResources.get(uiresource);
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

    // region LoginStatus

    public MaUiManager setLoggedInStatus(int resource) {
        lazyViewsSetup();
        setUserPicture(resource);
        return this;
    }

    public MaUiManager setLoggedIn(int resource) {
        lazyViewsSetup();
        setUserPicture(resource);
        return this;
    }

    public MaUiManager setLoggedIn(String resource) {
        lazyViewsSetup();
        setUserPicture(resource);
        return this;
    }

    public MaUiManager setLoggedOut() {
        lazyViewsSetup();
        drawerManager.resetIcon();
        isUserInfo = false;
        authenticatedView.setVisibility(View.INVISIBLE);
        return this;
    }

    // endregion

    // region EmptyView

    public MaUiManager setEmpty(boolean isEmpty) {
        if (isEmpty)
            setEmpty();
        else
            setNotEmpty();
        return this;
    }

    public MaUiManager setEmpty() {
        lazyViewsSetup();
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        return this;
    }

    public MaUiManager setNotEmpty() {
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

        uiResources.put(UIRESOURCE.DRAWER_LAYOUT, R.id.drawer_layout);
        uiResources.put(UIRESOURCE.DRAWER_MENU_LIST, R.id.ma_menuList);
        uiResources.put(UIRESOURCE.USER_PICTURE, R.id.ma_userImage);
        uiResources.put(UIRESOURCE.AUTHENTICATED_PICTURE, R.id.ma_authenticatedImage);
        uiResources.put(UIRESOURCE.RECYCLER_VIEW, R.id.ma_recyclerView);
        uiResources.put(UIRESOURCE.EMPTY_VIEW, R.id.ma_emptyView);
        uiResources.put(UIRESOURCE.PROGRESSBAR, R.id.ma_progressBar);

        recyclerView = setup(UIRESOURCE.RECYCLER_VIEW, RecyclerView.class, uiViews);
        emptyView = setup(UIRESOURCE.EMPTY_VIEW, TextView.class, uiViews);
        progressBar = setup(UIRESOURCE.PROGRESSBAR, ProgressBar.class, uiViews);
        userView = setup(UIRESOURCE.USER_PICTURE, ImageView.class, uiViews);
        authenticatedView = setup(UIRESOURCE.AUTHENTICATED_PICTURE, ImageView.class, uiViews);

        isViewsSet = true;
    }

    // endregion
}
