package pt.isel.pdm.g04.pf.cache;

import android.util.LruCache;

import pt.isel.pdm.g04.pf.helpers.Logger;

public abstract class MemoryCache<T> {

    private LruCache<String, T> cache;
    private int mCacheSize;

    public MemoryCache(int cacheSize) {
        mCacheSize = cacheSize;
        setLimit(cacheSize);
    }

    public void setLimit(int cacheSize) {
        if (cacheSize == mCacheSize && cache != null)
            return;

        if (cache != null)
            cache.evictAll();

        // cacheSize vem já em KiB logo só multiplicamos 1 vez
        cache = new LruCache<String, T>(Math.max(1, cacheSize * 1024)) {
            @Override
            protected int sizeOf(String key, T bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return getSizeInBytes(bitmap);
            }
        };
        Logger.i("[Memory] Cache will use up to " + cache.maxSize() / 1024 / 1024. + " MiB");
    }

    public T get(String key) {
        T res = cache.get(key);
        if (res == null)
            Logger.w("[Memory] Cache miss for " + key);
        else
            Logger.i("[Memory] Cache hit for " + key);
        return res;
    }

    public void put(String key, T obj) {
        if (obj==null)
            return;
        cache.put(key, obj);
        Logger.i("[Memory] Cache is using " + cache.size() / 1024 / 1024. + " MiB");
    }

    public void clear() {
        cache.evictAll();
    }

    abstract int getSizeInBytes(T bitmap);
}