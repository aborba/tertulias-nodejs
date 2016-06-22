package pt.isel.s1516v.ps.apiaccess.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.common.base.Predicate;

public class LoginStatus {

    public enum Status { SIGNED_IN, SIGNED_OUT}

    public static final boolean SIGNED_IN = true;
    public static final boolean SIGNED_OUT = false;

    private Context ctx;
    private ImageView view;
    private Predicate predicate;

    public LoginStatus(@NonNull Context ctx, ImageView view, @NonNull Predicate predicate) {
        this.ctx = ctx;
        this.view = view;
        this.predicate = predicate;
    }

    public void update() {
        set(predicate.apply(null));
    }

    public void set(boolean isLogin) { view.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE); }

    public void set(Status status) { set(status == Status.SIGNED_IN); }

    public void set() { set(Status.SIGNED_IN); }

    public void set(int message) {
        set();
        Util.longToast(ctx, message);
    }

    public void reset() { set(Status.SIGNED_OUT); }

    public void reset(int message) {
        reset();
        Util.longToast(ctx, message);
    }

}
