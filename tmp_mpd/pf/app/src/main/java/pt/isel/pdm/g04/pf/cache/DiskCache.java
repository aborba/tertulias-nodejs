package pt.isel.pdm.g04.pf.cache;

import java.io.File;
import java.io.IOException;

import pt.isel.pdm.g04.pf.helpers.Logger;

public abstract class DiskCache<T> {

    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    protected DiskLruCache diskCache;
    private File mCacheDir;
    private int mCacheSize;

    public DiskCache(File cacheDir, int cacheSize) {
        mCacheDir = cacheDir;
        mCacheSize = cacheSize;
        open(mCacheDir);

    }

    public void setLimit(int cacheSize) {
        if (cacheSize == mCacheSize)
            return;

        try {
            diskCache.close();
        } catch (IOException e) {
            Logger.e(e);
        }
        mCacheSize = cacheSize;
        open(mCacheDir);
    }


    private void open(File cacheDir) {
        try {
            if (!mCacheDir.exists())
                mCacheDir.mkdirs();
            diskCache = DiskLruCache.open(cacheDir, APP_VERSION, VALUE_COUNT, Math.max(1, mCacheSize * 1024));
            Logger.i("[Disk] Cache will use up to " + diskCache.maxSize() / 1024 / 1024. + " MiB");
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    public abstract T get(String key);

    public abstract void put(String key, T obj);


    public void clear() {
        if (diskCache == null) {
            return;
        }

        try {
            diskCache.delete();
            open(mCacheDir);
        } catch (IOException e) {
            Logger.e(e);
        }

    }

    public void flush() {
        try {
            diskCache.flush();
        } catch (IOException e) {
            Logger.e(e);
        }
    }
}