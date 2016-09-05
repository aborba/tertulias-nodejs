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

package pt.isel.s1516v.ps.apiaccess.memberinvitation.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.common.base.Function;

import java.util.EnumMap;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreation;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiAddress;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class MiUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        SEARCH,
        PROGRESBAR,
        EMPTY,
        RECYCLE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    public final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    private Toolbar toolbarView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private RecyclerView listView;

    public MiUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.sca_toolbar);
        uiResources.put(UIRESOURCE.SEARCH, R.id.sca_search);
        uiResources.put(UIRESOURCE.PROGRESBAR, R.id.sca_progressbar);
        uiResources.put(UIRESOURCE.EMPTY, R.id.sca_empty_view);
        uiResources.put(UIRESOURCE.RECYCLE, R.id.sca_RecyclerView);
    }

    public void set(TertuliaCreation tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
    }

    public View getView(UIRESOURCE uiresource) {
        lazyViewsSetup();
        return uiViews.get(uiresource);
    }

    // region UiManager

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

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
    public boolean isLongitude() {
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

    // region public static methods

    @Override
    protected int getUiResource(String resource) {
        return uiResources.get(UIRESOURCE.valueOf(resource));
    }

    // endregion

    // region private methods

    private void lazyViewsSetup() {
        if (isViewsSet)
            return;
        toolbarView = setup(UIRESOURCE.TOOLBAR, Toolbar.class, uiViews);
        searchView = setup(UIRESOURCE.SEARCH, SearchView.class, uiViews);
        progressBar = setup(UIRESOURCE.PROGRESBAR, ProgressBar.class, uiViews);
        emptyView = setup(UIRESOURCE.EMPTY, TextView.class, uiViews);
        listView = setup(UIRESOURCE.RECYCLE, RecyclerView.class, uiViews);
        listView.setHasFixedSize(true);
    }

    private void fillInViews(TertuliaCreation tertulia) {
    }

    // endregion

}