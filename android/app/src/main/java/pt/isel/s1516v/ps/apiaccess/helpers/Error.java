package pt.isel.s1516v.ps.apiaccess.helpers;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import pt.isel.s1516v.ps.apiaccess.R;

public class Error {
    private static final Map<String, Integer> statusCodeMessages = new HashMap<>();
    static {
        statusCodeMessages.put("401", R.string.main_activity_status_code_401);
    }

    @com.google.gson.annotations.SerializedName("code")
    String statusCode;

    public String getStatusCodeMessage(Context ctx) {
        return ctx.getResources().getString(statusCodeMessages.get(statusCode));
    }
}
