package pt.isel.pdm.g04.pf.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pt.isel.pdm.g04.pf.BuildConfig;
import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.data.Notification;
import pt.isel.pdm.g04.pf.presentation.MainActivity;
import pt.isel.pdm.g04.pf.presentation.MapActivity;
import pt.isel.pdm.g04.pf.workers.Task;

public class Utils {
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public static void assertNotOnUIThread() {
        if (BuildConfig.DEBUG && isUiThread()) {
            throw new IllegalThreadStateException("This should not be Running on the UI thread!");
        }
    }

    public static void showMarkers(final Context context, final GoogleMap map, final List<Notification> notifications) {
        if (notifications == null)
            return;

        final View markerView = ((LayoutInflater) context
                .getSystemService(
                        context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.map_marker, null);
        final ImageView pin = (ImageView) markerView.findViewById(R.id.pin);
        final ImageView profile = (ImageView) markerView.findViewById(R.id.profile);
        for (final Notification n : notifications) {

            final Task<Bitmap> task = new Task<Bitmap>() {

                @Override
                public void run() {
                    synchronized (MapActivity.class) {
                        pin.setColorFilter(n.getColor());
                        profile.setImageBitmap(Utils.getCircularBitmap(res));
                        BitmapDescriptor bd = BitmapDescriptorFactory
                                .fromBitmap(Utils.createDrawableFromView(
                                        context,
                                        markerView));
                        LatLng latLng = n.getCoordinates();
                        map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(n.getTeacherName())
                                .snippet(Utils.getFriendlyDate(context, new Date(n.getTime())))
                                .icon(bd));
                    }
                }
            };
            task.url = n.getTeacherAvatarUrl();
            TeacherLocatorApplication.sIOThread.queueImageRead(task);
        }
    }

    public static void openMap(Context context, Notification notification) {
        openMap(context, new ArrayList<>(Arrays.asList(notification)));
    }

    public static void openMap(Context context, ArrayList<Notification> notifications) {

        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(Constants.Activities.NOTIFICATIONS_EXTRA, notifications);
        context.startActivity(intent);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static void share(Context context, Bitmap icon, String text, String subject) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            Logger.e(e);
        }
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.notification_share) + " " + subject));
    }


    public static String getFriendlyLocation(Context context, String location) {
        if (TextUtils.isEmpty(location))
            return "";
        switch (location) {
            case Constants.Isel.Locations.BUILDING_A:
                return context.getResources().getString(R.string.location_building_a);
            case Constants.Isel.Locations.BUILDING_C:
                return context.getResources().getString(R.string.location_building_c);
            case Constants.Isel.Locations.BUILDING_E:
                return context.getResources().getString(R.string.location_building_e);
            case Constants.Isel.Locations.BUILDING_F:
                return context.getResources().getString(R.string.location_building_f);
            case Constants.Isel.Locations.BUILDING_G:
                return context.getResources().getString(R.string.location_building_g);
            case Constants.Isel.Locations.BUILDING_M:
                return context.getResources().getString(R.string.location_building_m);
            case Constants.Isel.Locations.BUILDING_P:
                return context.getResources().getString(R.string.location_building_p);
            case Constants.Isel.Locations.CC:
                return context.getResources().getString(R.string.location_cc);
            case Constants.Isel.Locations.GYM:
                return context.getResources().getString(R.string.location_gym);
            case Constants.Isel.Locations.RESIDENCE:
                return context.getResources().getString(R.string.location_residence);
            case Constants.Isel.Locations.STUDENT_PAVILLION:
                return context.getResources().getString(R.string.location_student_pavillion);
            case Constants.Isel.Locations.OTHER:
                return context.getResources().getString(R.string.location_isel);
            default:
                return location;
        }

    }

    public static String getFriendlyDate(Context context, Date startDate) {

        Date endDate = new Date();
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;

        if (elapsedHours > 0) {
            return String.format(context.getResources().getString(R.string.notification_last_seen_hours), elapsedHours);
        }

        different = different % hoursInMilli;

        long elapsedMinutes = Math.max(1, different / minutesInMilli);
        different = different % minutesInMilli;

        return String.format(context.getResources().getString(R.string.notification_last_seen_minutes), elapsedMinutes);
    }

    public static Bitmap getCircularBitmap(Bitmap bm) {
        if (bm == null) {
            return bm;
        }
        int sice = Math.min((bm.getWidth()), (bm.getHeight()));
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void sendEmailMessage(Context context, String subject, String body, String chooserTitle, String[] to) {
        Intent mailIntent = new Intent();
        mailIntent.setAction(Intent.ACTION_SEND);
        mailIntent.setType("message/rfc822");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(mailIntent, chooserTitle));
    }

    public static File getCacheDir(Context context) {
        //Find the dir to save cached images
        File res;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            res = new File(android.os.Environment.getExternalStorageDirectory(), context.getPackageName());
        res = context.getCacheDir();
        Logger.i("[Disk] Cache folder:" + res.getAbsolutePath());
        return res;

    }

    private static boolean isUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static void assertOnlyOnUIThread() {
        if (BuildConfig.DEBUG && !isUiThread()) {
            throw new IllegalThreadStateException("This must run in the UI thread!");
        }
    }

    public static boolean fakeTeacher(String email) {
        return Constants.Thoth.DEBUG_ALWAYS_LOGIN_AS_TEACHER ||
                email.matches("^\\d.*-teacher@alunos.isel.ipl.pt");
    }

    public static String getEmail() {

        if (Constants.Thoth.DEBUG_ALWAYS_LOGIN_AS_TEACHER)
            return Constants.Thoth.DEBUG_TEACHER_EMAIL;

        String email = MainActivity.account != null ?
                MainActivity.account.name : null;
        return email;
    }

    // region toasts

    public static void shortToast(Context ctx, int msg) {
        assertOnlyOnUIThread();
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void shortToast(Context ctx, String msg) {
        assertOnlyOnUIThread();
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context ctx, int msg) {
        assertOnlyOnUIThread();
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void longToast(Context ctx, String msg) {
        assertOnlyOnUIThread();
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    // endregion

    // region No Null Allowed

    public static String coalesce(String defaultString, String backupString) {
        return TextUtils.isEmpty(defaultString) ? backupString : defaultString;
    }

    // endregion

    // region Mangle

    public static String mangleEmail(String email) {
        return email.replace("@", "_AT_").replaceAll("\\.", "_DOT_");
    }

    public static String demangleEmail(String mangledEmail) {
        return mangledEmail.replace("_AT_", "@").replaceAll("_DOT_", ".");
    }

    // endregion

    // region Activity visibility

    private static boolean isActivityVisible = false;

    public static boolean isActivityVisible() {
        return isActivityVisible;
    }

    public static void setActivityVisible(boolean isVisible) {
        isActivityVisible = isVisible;
    }

    // endregion

}
