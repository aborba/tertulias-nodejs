package pt.isel.pdm.g04.pf.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class WebRequest {

    private final String mUrl;
    private HttpURLConnection mConn;
    private Gson mGson;
    private static final int MAX_AVAILABLE = 30;
    private final Semaphore mAvailable = new Semaphore(MAX_AVAILABLE, true);

    private WebRequest(String url) {
        mUrl = url;
    }

    public static WebRequest connect(String url) {
        return new WebRequest(url);
    }

    public <T> T to(Class<T> tClass) {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson.fromJson(get(), tClass);
    }

    public Bitmap toBitmap() {
        try {
            InputStream stream = (InputStream) mConn.getContent();
            Bitmap res = BitmapFactory
                    .decodeStream(stream);
            mConn.disconnect();
            return res;

        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }


    public WebRequest download() {
        Utils.assertNotOnUIThread();
        mAvailable.acquireUninterruptibly();
        try {
            Logger.i("Downloading from " + mUrl);
            URL url = new URL(mUrl);
            mConn = (HttpURLConnection) url.openConnection(); // Cast shouldn't fail
            HttpURLConnection.setFollowRedirects(true);
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            mAvailable.release();
        }
        return this;
    }

    public String get() {
        Utils.assertNotOnUIThread();
        InputStream inStr = null;
        BufferedReader br;
        String line;

        try {


// allow both GZip and Deflate (ZLib) encodings
            mConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            String encoding = mConn.getContentEncoding();


// create the appropriate stream wrapper based on
// the encoding type
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                inStr = new GZIPInputStream(mConn.getInputStream());
            } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
                inStr = new InflaterInputStream(mConn.getInputStream(),
                        new Inflater(true));
            } else {
                inStr = mConn.getInputStream();
            }
            br = new BufferedReader(new InputStreamReader(inStr));

            StringBuilder res = new StringBuilder();

            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            return res.toString();
        } catch (Exception ioe) {
            Logger.e(ioe);
        } finally {
            try {
                if (inStr != null) inStr.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
            mConn.disconnect();
        }
        return null;
    }

}

