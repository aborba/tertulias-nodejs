package pt.isel.pdm.g04.pf.cache;

import android.graphics.Bitmap;

public class MemoryImageCache extends MemoryCache<Bitmap> {

    public MemoryImageCache(int cacheSize) {
        super(cacheSize);
    }

    @Override
    int getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
