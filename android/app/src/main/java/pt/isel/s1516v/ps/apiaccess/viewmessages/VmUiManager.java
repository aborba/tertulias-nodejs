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

package pt.isel.s1516v.ps.apiaccess.viewmessages;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class VmUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        PROGRESSBAR,
        MESSAGE
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    private Toolbar toolbarView;
    private EditText messageView;
    private ProgressBar progressBar;

    public VmUiManager(Context ctx) {
        super(ctx);
    }

    public void set(TertuliaMessage tertulia) {
        lazyViewsSetup();
        fillInViews(tertulia);
    }

    public TertuliaMessage get() {
        lazyViewsSetup();
        TertuliaMessage tertulia = new TertuliaMessage(messageView.getText().toString());
        return tertulia;
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
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

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
        return false;
    }

    @Override
    public boolean isLongitude() {
        return false;
    }

    @Override
    public String getLatitudeData() {
        return null;
    }

    @Override
    public String getLongitudeData() {
        return null;
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
        uiResources.put(UIRESOURCE.TOOLBAR, R.id.toolbar);
        uiResources.put(UIRESOURCE.PROGRESSBAR, R.id.sm_progressBar);
        uiResources.put(UIRESOURCE.MESSAGE, R.id.sm_message);

        toolbarView = setup(UIRESOURCE.TOOLBAR, Toolbar.class, uiViews);
        progressBar = setup(UIRESOURCE.PROGRESSBAR, ProgressBar.class, uiViews);
        messageView = setup(UIRESOURCE.MESSAGE, EditText.class, uiViews);
        isViewsSet = true;
    }

    private void fillInViews(TertuliaMessage tertulia) {
        lazyViewsSetup();
        messageView.setText(tertulia.message);
    }

    // endregion

}