package pt.isel.s1516v.ps.apiaccess.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.CircleTransform;

public class DrawerManager {
    private final Context ctx;
    public final DrawerLayout layout;
    private final ListView menuView;
    private final ImageView userImage;

    public DrawerManager(Context ctx, DrawerLayout layout, ListView menuView, ImageView userImage) {
        this.ctx = ctx;
        this.layout = layout;
        this.menuView = menuView;
        this.userImage = userImage;
    }

    public DrawerManager(Context ctx, int rLayout, int rMenuView, int rUserImage) {
        this(ctx,
                (DrawerLayout) ((Activity) ctx).findViewById(rLayout),
                (ListView) ((Activity) ctx).findViewById(rMenuView),
                (ImageView) ((Activity) ctx).findViewById(rUserImage));
    }

    public DrawerManager prepareMenu(String[] menu, ListView.OnItemClickListener listener) {
        if (menuView == null)
            throw new IllegalStateException("Menu no defined.");
        menuView.setAdapter(new ArrayAdapter<>(ctx,
                android.R.layout.simple_list_item_1, menu));
        menuView.setOnItemClickListener(listener);
        return this;
    }

    public DrawerManager prepareMenu(int menuResource, ListView.OnItemClickListener listener) {
        String[] menu = ctx.getResources().getStringArray(menuResource);
        return prepareMenu(menu, listener);
    }

    // region Drawer behaviour

    public void open() {
        layout.openDrawer(GravityCompat.START);
    }

    public void close() {
        layout.closeDrawer(GravityCompat.START);
    }

    public boolean isOpen() {
        return layout.isDrawerOpen(GravityCompat.START);
    }

    // endregion

    // region Icon Management

    public void setIcon(int resource) {
        Picasso.with(ctx).load(resource).into(userImage);
    }

    public void setIcon(String resource) {
        if (TextUtils.isEmpty(resource))
            return;
        Picasso.with(ctx).load(resource).transform(new CircleTransform()).into(userImage);
        userImage.setVisibility(View.VISIBLE);
    }

    public void resetIcon() {
        setIcon(R.mipmap.tertulias);
    }

    // endregion

}
