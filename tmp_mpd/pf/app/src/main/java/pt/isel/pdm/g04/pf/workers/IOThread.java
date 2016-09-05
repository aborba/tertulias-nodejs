package pt.isel.pdm.g04.pf.workers;

import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.cache.DiskImageCache;
import pt.isel.pdm.g04.pf.cache.MemoryImageCache;
import pt.isel.pdm.g04.pf.helpers.Logger;

public class IOThread extends BaseThread {

    private static final int TYPE_READ = 0;
    private static final int TYPE_WRITE = 1;
    private static final String TAG = IOThread.class.getSimpleName();
    private final MemoryImageCache mImageCache;
    private final DiskImageCache mDiskCache;
    protected Map<String, Bitmap> mPendingWritesMap = new ConcurrentHashMap<>();


    public IOThread(File cacheDir, int diskCacheSize, int memoryCacheSize) {
        super(TAG);
        mDiskCache = new DiskImageCache(cacheDir, diskCacheSize);
        mImageCache = new MemoryImageCache(memoryCacheSize);
    }

    public void flush() {
        mDiskCache.flush();
    }

    public void queueImageRead(Task<Bitmap> task) {
        if (task == null || TextUtils.isEmpty(task.url))
            return;
        Logger.i("[" + TAG + "] added " + task.url + " to the queue (READ).");
        mRequestMap.put(task.url, task);
        mWorkerHandler.obtainMessage(TYPE_READ, task.url)
                .sendToTarget();
    }

    public void queueImageWrite(String url, Bitmap bmp) {
        Logger.i("[" + TAG + "] added " + url + " to the queue (WRITE).");
        mPendingWritesMap.put(url, bmp);
        mWorkerHandler.obtainMessage(TYPE_WRITE, url)
                .sendToTarget();
    }

    public void clearCache() {
        mDiskCache.clear();
        mImageCache.clear();
    }

    public void resizeCache(int diskCacheSize, int memoryCacheSize) {
        mDiskCache.setLimit(diskCacheSize);
        mImageCache.setLimit(memoryCacheSize);
    }

    @Override
    void handleMessageInternal(Message msg) {
        final String url = (String) msg.obj;
        int type = msg.what;


        if (type == TYPE_READ) {
            final Task<Bitmap> task = (Task<Bitmap>) mRequestMap.get(url);
            if (task == null)
                return;
            mRequestMap.remove(url);
            Bitmap bitmap = mImageCache.get(url);

            if (bitmap == null) {
                bitmap = mDiskCache.get(url);
                if (bitmap != null) {
                    mImageCache.put(url, bitmap);
                }
            }

            if (bitmap != null) {
                task.res = bitmap;
                mResponseHandler.postAtFrontOfQueue(task);
            } else {
                TeacherLocatorApplication.sDownloadThread.queueImageDownload(task);
            }

        } else if (type == TYPE_WRITE) {
            final Bitmap bmp = mPendingWritesMap.get(url);
            mPendingWritesMap.remove(url);
            mImageCache.put(url, bmp);
            mDiskCache.put(url, bmp);

        }
    }
}
