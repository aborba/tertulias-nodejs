package pt.isel.pdm.g04.pf.presentation.widget.recyclerview.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.data.Notification;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Preferences;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.workers.Task;

public class NotificationsAdapter extends CursorRecyclerAdapter<NotificationsAdapter.ViewHolder> {

    private CursorAdapter mCursorAdapter;
    private Context mContext;

    public NotificationsAdapter(final RecyclerView recyclerView, Cursor c) {
        super(c);
        mContext = recyclerView.getContext();
        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = LayoutInflater.from(context).inflate(R.layout.notification_item, null);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
                return view;
            }

            @Override
            public void bindView(final View view, final Context context, Cursor cursor) {
                final Notification item = new Notification(cursor);

                final ViewHolder viewHolder = (ViewHolder) view.getTag();
                final Task<Bitmap> task = new Task<Bitmap>() {

                    @Override
                    public void run() {
                        if (updateImageView(url, viewHolder.ivImage, this.res) && Preferences.useColoredNotifications(mContext)) {
                            if (item.getColor()!=-1)
                            {
                                viewHolder.ivImage.setColorFilter(item.getColor());
                            }
                        }
                    }
                };
                task.url = item.getTeacherAvatarUrl();
                TeacherLocatorApplication.sIOThread.queueImageRead(task);
                viewHolder.ivImage.setTag(task.url);
                final String name = item.getTeacherName();
                viewHolder.tvTitle.setText(name);
                viewHolder.tvDescription.setText(item.getFriendlyLocation(context) + ", " + Utils.getFriendlyDate(context, new Date(item.getTime())));

                viewHolder.bEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.sendEmailMessage(mContext, mContext.getResources().getString(R.string.app_name), "",
                                mContext.getResources().getString(R.string.notification_email) + " " + name,
                                new String[]{item.getTeacherEmail()});
                    }
                });

                viewHolder.bOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.openMap(mContext, item);
                    }
                });

                viewHolder.bShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap icon = ((BitmapDrawable) viewHolder.ivImage.getDrawable()).getBitmap();
                        String text = viewHolder.tvDescription.getText().toString();
                        Utils.share(mContext, icon, text, name);
                    }
                });
            }
        };

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mCursorAdapter.newView(mContext, mCursor, viewGroup);
        ViewHolder holder = (ViewHolder) view.getTag();
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        mCursorAdapter.bindView(viewHolder.itemView, mContext, cursor);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvDescription;
        Button bEmail;
        Button bOpen;
        Button bShare;

        public ViewHolder(View itemView) {

            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.profile_image);
            tvTitle = (TextView) itemView.findViewById(R.id.user_name);
            tvDescription = (TextView) itemView.findViewById(R.id.details);
            bEmail = (Button) itemView.findViewById(R.id.action_email);
            bOpen = (Button) itemView.findViewById(R.id.action_open);
            bShare = (Button) itemView.findViewById(R.id.action_share);

        }
    }

    private boolean updateImageView(String url, ImageView view, Bitmap bitmap) {
        if (view == null || bitmap == null)
            return false;

        String currentUrl = (String) view.getTag();
        if (!url.equals(currentUrl)) {
            Logger.i("View already reused for url: " + url);
            return false;
        } else {
            view.setImageBitmap(Utils.getCircularBitmap(bitmap));
        }
        return true;
    }

}
