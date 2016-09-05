package pt.isel.pdm.g04.pf.workers;

import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;

import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.WebRequest;

public class DownloadThread extends BaseThread {

    private static final int TYPE_BITMAP = 0;
    private static final int TYPE_JSON = 1;
    private static final String TAG = DownloadThread.class.getSimpleName();

    public DownloadThread() {
        super(TAG);
    }

    @Override
    void handleMessageInternal(Message msg) {
        String url = (String) msg.obj;
        handleRequest(url, msg.what);
    }

    public void queueImageDownload(Task<Bitmap> task) {
        String url = task.url;
        if (TextUtils.isEmpty(url))
            return;
        mRequestMap.put(url, task);
        queueTask(url, TYPE_BITMAP);
    }

    public void queueTeacherDetailsDownload(Task<Teacher> task) {
        String url = task.url;
        if (TextUtils.isEmpty(url))
            return;
        mRequestMap.put(url, task);
        queueTask(url, TYPE_JSON);
    }

    private void queueTask(String url, int type) {
        Logger.i("[" + TAG + "] added " + url + " to the queue.");
        mWorkerHandler.obtainMessage(type, url)
                .sendToTarget();
    }

    private void handleRequest(final String url, final int type) {
        final WebRequest request = WebRequest.connect(url)
                .download();

        if (type == TYPE_BITMAP) {
            final Bitmap bitmap = request.toBitmap();
            if (bitmap == null)
                return;

            final Task<Bitmap> task = (Task<Bitmap>) mRequestMap.get(url);
            mRequestMap.remove(url);
            if (task == null) {
                return;
            }
            task.res = bitmap;
            mResponseHandler.postAtFrontOfQueue(task);
            TeacherLocatorApplication.sIOThread.queueImageWrite(url, bitmap);
        } else if (type == TYPE_JSON) {
            final Teacher teacher = request.to(Teacher.class);
            final Task<Teacher> task = (Task<Teacher>) mRequestMap.get(url);
            mRequestMap.remove(url);
            if (task == null) {
                return;
            }
            task.res = teacher;
            mResponseHandler.post(task);

        }
    }

}