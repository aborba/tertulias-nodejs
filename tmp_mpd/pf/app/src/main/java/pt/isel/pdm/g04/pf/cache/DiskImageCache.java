package pt.isel.pdm.g04.pf.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class DiskImageCache extends DiskCache<Bitmap> {

    public DiskImageCache(File cacheDir, int cacheSize) {
        super(cacheDir, cacheSize);
    }

    @Override
    public Bitmap get(String key) {
        if (diskCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = diskCache.get(getNormalizedKey(key));
            if (snapshot == null) {
                Logger.w("[Disk] Cache miss for " + key);
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn = new BufferedInputStream(in, Utils.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
                Logger.i("[Disk] Cache hit for " + key);
            }
        } catch (final IOException e) {
            Logger.e(e);
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return bitmap;
    }

    @Override
    public void put(String key, Bitmap obj) {
        if (diskCache == null) {
            return;
        }

        DiskLruCache.Editor editor = null;
        try {
            editor = diskCache.edit(getNormalizedKey(key));
            if (editor == null)
                return;

            if (writeBitmapToFile(obj, editor)) {
                editor.commit();
                Logger.i("[Disk] image put on disk cache " + key);
                Logger.i("[Disk] Cache is using " + diskCache.size() / 1024 / 1024. + " MiB");

            } else {
                editor.abort();
                Logger.w("[Disk] ERROR on: image put on disk cache " + key);
            }
        } catch (final IOException e) {
            Logger.e(e);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (final IOException | IllegalStateException ignored) {
            }
        }
    }


    private String getNormalizedKey(String key) {
        return String.valueOf(key.hashCode());
    }

    private boolean writeBitmapToFile(final Bitmap bitmap, final DiskLruCache.Editor editor) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), Utils.IO_BUFFER_SIZE);
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
