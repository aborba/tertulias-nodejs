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

package pt.isel.s1516v.ps.apiaccess.about;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.CircleTransform;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class AbUiManager extends UiManager {

    public enum UIRESOURCE {
        TOOLBAR,
        PICTURE,
        TEXTVIEW
    }

    private final EnumMap<UIRESOURCE, Integer> uiResources = new EnumMap<>(UIRESOURCE.class);
    private final EnumMap<UIRESOURCE, View> uiViews = new EnumMap<>(UIRESOURCE.class);
    private boolean isViewsSet;

    private Toolbar toolbarView;
    private TextView textView;
    private ImageView picture;

    private static final String aboutText = "This app is part of the \"Tertulias Project\" which " +
            "was designed and developed by António Borba da Silva, under the supervision of " +
            "Eng. Pedro Félix from ISEL, Portugal.\n" +
            "\n" +
            "The project was developed within the scope of the \"Project and Seminar\" lecture of " +
            "the Computer Engineering and Computer Science course at ISEL, in the summer of 2016.\n" +
            "\n" +
            "In addition to this app, the project includes a NodeJS based server running in " +
            "Microsoft's Azure cloud, from where data is served and persisted, and makes use of " +
            "several Google's cloud services (for user authentication, maps, places and push " +
            "notifications).\n" +
            "The server has a public swagger based API which documented at " +
            "<http://tertulias.azuresweb.net/docs/api>.\n" +
            "Note: The app tracks user's GPS position.\n" +
            "\n" +
            "The system is made available \"as is\", without warranty of any kind, express or " +
            "implied, including but not limited to the warranties of merchantability, fitness for " +
            "a particular purpose and noninfringement and it can be discontinued at anytime " +
            "without previous warning. In no event shall the authors or copyright holders be " +
            "liable for any claim, damages or other liability, whether in an action of contract, " +
            "tort or otherwise, arising from, out of or in connection with the app or the software " +
            "or the use or other dealings in the app or in the software.\n" +
            "\n" +
            "The author can be contacted at antonio.borba[AT]gmail.com\n";

    public AbUiManager(Context ctx) {
        super(ctx);
    }

    public void set() {
        lazyViewsSetup();
        fillInViews();
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
    }

    @Override
    public void hideProgressBar() {
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
        uiResources.put(UIRESOURCE.TEXTVIEW, R.id.ab_textView);
        uiResources.put(UIRESOURCE.PICTURE, R.id.ab_picture);

        toolbarView = setup(UIRESOURCE.TOOLBAR, Toolbar.class, uiViews);
        picture = setup(UIRESOURCE.PICTURE, ImageView.class, uiViews);
        textView = setup(UIRESOURCE.TEXTVIEW, TextView.class, uiViews);
        isViewsSet = true;
    }

    private void fillInViews() {
        lazyViewsSetup();
        textView.setText(aboutText);
        Picasso.with(ctx).load(R.mipmap.tertulias).transform(new CircleTransform()).into(picture);
    }

    // endregion

}