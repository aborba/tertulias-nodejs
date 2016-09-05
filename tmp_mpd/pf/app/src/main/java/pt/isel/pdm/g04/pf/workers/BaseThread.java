package pt.isel.pdm.g04.pf.workers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseThread extends HandlerThread {

    protected Handler mWorkerHandler;
    protected Handler mResponseHandler;
    protected Map<String, Task<?>> mRequestMap = new ConcurrentHashMap<>();

    protected BaseThread(String name) {
        super(name);
        mResponseHandler = new Handler(Looper.getMainLooper());;
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                handleMessageInternal(msg);
                return true;
            }
        });
    }

    public int getActiveJobCount()
    {
        return mRequestMap.size();
    }

    abstract void handleMessageInternal(Message message);

}
