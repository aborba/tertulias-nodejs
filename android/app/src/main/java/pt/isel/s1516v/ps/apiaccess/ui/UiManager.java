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
import android.view.View;

import java.util.EnumMap;

public abstract class UiManager {
    final protected Context ctx;
    final protected Activity activity;
    private View rootView;

    public UiManager(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
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

    protected View findViewById(int resource) {
        return activity.findViewById(resource);
    }

    protected <T extends Enum<T>> View findView(T resource) {
        return findViewById(getUiResource(resource.name()));
    }

    protected abstract int getUiResource(String resource);

    protected  <T extends View, U extends Enum<U>> T setup(U resource, Class<T> viewType, EnumMap<U, View> uiViews) {
        T view = (T) findView(resource);
        uiViews.put(resource, view);
        return view;
    }

}
