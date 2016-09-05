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

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.ApiMember;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.MembersViewArrayAdapter;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class VmUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        PROGRESSBAR,
        TOTALS,
        EMPTY,
        RECYCLE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    public final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    private Toolbar toolbarView;
    private ProgressBar progressBar;
    private TextView totalsView;
    private TextView emptyView;
    private RecyclerView recyclerView;

    public VmUiManager(Context ctx) {
        super(ctx);
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.toolbar);
        uiResources.put(UIRESOURCE.PROGRESSBAR, R.id.vma_progressbar);
        uiResources.put(UIRESOURCE.TOTALS, R.id.vma_members_count);
        uiResources.put(UIRESOURCE.EMPTY, R.id.vma_empty_view);
        uiResources.put(UIRESOURCE.RECYCLE, R.id.vma_RecyclerView);
    }

    public void set(ApiMember[] members) {
        lazyViewsSetup();
        MembersViewArrayAdapter arrayAdapter = new MembersViewArrayAdapter((Activity) ctx, members);
        swapAdapter(arrayAdapter);
        if (members == null || members.length == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public void set(int totals) {
        lazyViewsSetup();
        if (totals == 1)
            return;
        String template = ctx.getResources().getString(R.string.view_member_users_count);
        totalsView.setText(String.format(Locale.getDefault(), template, totals));
    }

    public void swapAdapter(MembersViewArrayAdapter viewAdapter) {
        lazyViewsSetup();
        Util.setupAdapter((Activity) ctx, recyclerView, viewAdapter);
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
        progressBar = setup(UIRESOURCE.PROGRESSBAR, ProgressBar.class, uiViews);
        totalsView = setup(UIRESOURCE.TOTALS, TextView.class, uiViews);
        emptyView = setup(UIRESOURCE.EMPTY, TextView.class, uiViews);
        recyclerView = setup(UIRESOURCE.RECYCLE, RecyclerView.class, uiViews);
        recyclerView.setHasFixedSize(true);
    }

    private void fillInViews(ApiMember[] members) {
    }

    // endregion

}