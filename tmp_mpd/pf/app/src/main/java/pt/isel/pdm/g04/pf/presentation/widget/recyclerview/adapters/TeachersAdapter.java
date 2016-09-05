package pt.isel.pdm.g04.pf.presentation.widget.recyclerview.adapters;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.data.thoth.database.Schema;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.workers.Task;

public class TeachersAdapter extends CursorRecyclerAdapter<TeachersAdapter.ViewHolder> {
    // PATCH: Because RecyclerView.Adapter in its current form doesn't natively support
    // cursors, we "wrap" a CursorAdapter that will do all the job
    // for us
    private final Drawable mCheckIcon;
    private final Cursor mCursor;

    private CursorAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;

    private Map<String, Boolean> mSelectedItems = new HashMap<>();

    private int imageBackgroundColorWhenChecked;
    private int mCheckedImageOverlayColor;
    private int padding;
    private int mImageBackgroundColor;
    private int mImageCheckColor;

    public TeachersAdapter(final RecyclerView recyclerView, Cursor c) {
        super(c);

        mContext = recyclerView.getContext();
        mRecyclerView = recyclerView;
        mCursor = c;
        mCheckIcon = createCheckIcon();

        initColors(mContext);

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = LayoutInflater.from(context).inflate(R.layout.teacher_item, null);
                ViewHolder holder = new ViewHolder(view);
                setCheckedState(holder, false);
                view.setTag(holder);
                return view;
            }

            @Override
            public void bindView(final View view, Context context, Cursor cursor) {
                view.setBackgroundColor(Color.TRANSPARENT);
                final ViewHolder holder = (ViewHolder) view.getTag();
                Teacher teacher=new Teacher(cursor);
                holder.imgThumbnail.setImageResource(R.drawable.profile);
                holder.tvName.setText(teacher.getShortName());
                holder.email = teacher.getAcademicEmail();
                String url = teacher.getAvatarUrl().getSize128();
                boolean detailsMissing = false;
                if (TextUtils.isEmpty(url)) {
                    url = teacher.getAvatarUrl().getSize32();
                    detailsMissing = true;
                }
                holder.imgThumbnail.setTag(url);
                if (!mSelectedItems.containsKey(holder.email)) {
                    mSelectedItems.put(holder.email, false);
                }
                setCheckedState(holder, mSelectedItems.get(holder.email));
                final Task<Bitmap> task = new Task<Bitmap>() {

                    @Override
                    public void run() {
                        updateImageView(url, holder.imgThumbnail, res);
                    }
                };
                task.url = url;

                TeacherLocatorApplication.sIOThread.queueImageRead(task);
                if (detailsMissing) {

                    final Task<Teacher> detailsTask = new Task<Teacher>() {
                        @Override
                        public void run() {
                            updateTeacher(holder.imgThumbnail, this.res);
                        }
                    };

                    detailsTask.url = teacher.get_links().getSelf();
                    TeacherLocatorApplication.sDownloadThread.queueTeacherDetailsDownload(detailsTask);
                }
            }
        };
    }

    public ArrayList<String> getSelectedTeachers() {
        ArrayList<String> res = new ArrayList<>();
        for (String i : mSelectedItems.keySet()) {
            if (mSelectedItems.get(i)) {
                res.add(i);
            }
        }
        return res;
    }

    public void setSelectedTeachers(ArrayList<String> selectedItems) {

        for (String i : selectedItems) {
            mSelectedItems.put(i, true);
        }
    }

    private void updateTeacher(final ImageView imageView, Teacher teacher) {
        if (imageView == null || teacher == null)
            return;

        String currentUrl = (String) imageView.getTag();
        if (currentUrl.equals(teacher.getAvatarUrl().getSize32())) {
            currentUrl = teacher.getAvatarUrl().getSize128();
            imageView.setTag(currentUrl);
            Task task = new Task<Bitmap>() {
                @Override
                public void run() {
                    updateImageView(url, imageView, res);
                }
            };
            task.url = currentUrl;
            TeacherLocatorApplication.sIOThread.queueImageRead(task);
        }

        ContentResolver contentResolver = imageView.getContext().getContentResolver();
        ContentProviderClient contentProviderClient = contentResolver.acquireContentProviderClient(ThothContract.AUTHORITY);
        try {
            Schema.Teachers.update(contentResolver, teacher);
            if (TeacherLocatorApplication.sDownloadThread.getActiveJobCount() == 0) {
                contentResolver.notifyChange(ThothContract.Teachers.CONTENT_URI, null);
            }
        } catch (ParseException | RemoteException e) {
            e.printStackTrace();
        }
        contentProviderClient.release();
    }

    private void updateImageView(String url, ImageView view, Bitmap bitmap) {
        if (view == null || bitmap == null)
            return;

        String currentUrl = (String) view.getTag();
        if (!url.equals(currentUrl)) {
            Logger.i("View already reused for url: " + url);
        } else {
            view.setImageBitmap(bitmap);
        }
    }

    private void initColors(Context context) {
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        imageBackgroundColorWhenChecked = typedValue.data;
        mCheckedImageOverlayColor = context.getResources().getColor(R.color.alter_checked_photo_overlay);
        padding = context.getResources().getDimensionPixelSize(R.dimen.image_checked_padding);
        mImageBackgroundColor = context.getResources().getColor(R.color.alter_unchecked_image_background);
        mImageCheckColor = context.getResources().getColor(R.color.alter_image_check_color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mCursorAdapter.newView(mContext, mCursor, viewGroup);
        ViewHolder holder = (ViewHolder) view.getTag();
        return holder;
    }

    private Drawable createCheckIcon() {
        Drawable checkIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_action_done_white);
        checkIcon = DrawableCompat.wrap(checkIcon);
        return checkIcon;
    }

    public void setHeight(final View convertView) {
        final int height = mRecyclerView.getMeasuredWidth() / mRecyclerView.getResources().getInteger(R.integer.num_columns_images);
        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        mCursorAdapter.bindView(viewHolder.itemView, mContext, cursor);
        setHeight(viewHolder.itemView);
    }

    public void setCheckedState(ViewHolder holder, boolean checked) {
        if (checked) {
            holder.imgCheck.setBackgroundColor(imageBackgroundColorWhenChecked);
            holder.imgThumbnail.setColorFilter(mCheckedImageOverlayColor);
            holder.imgThumbnail.setBackgroundColor(imageBackgroundColorWhenChecked);
            holder.itemView.setPadding(padding, padding, padding, padding);
        } else {
            holder.imgCheck.setBackgroundColor(mImageCheckColor);
            holder.imgThumbnail.setColorFilter(Color.TRANSPARENT);
            holder.itemView.setBackgroundColor(mImageBackgroundColor);
            holder.itemView.setPadding(0, 0, 0, 0);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgThumbnail;
        public ImageView imgCheck;
        public TextView tvName;

        public String email;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            imgCheck = (ImageView) itemView.findViewById(R.id.image_check);
            imgCheck.setImageDrawable(mCheckIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Boolean checked = !mSelectedItems.get(email);
            mSelectedItems.put(email, checked);
            setCheckedState(this, checked);
        }

    }

}
