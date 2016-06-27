package pt.isel.s1516v.ps.apiaccess.support.remote;

public class Status {
    @com.google.gson.annotations.SerializedName("code")
    final int code;
    @com.google.gson.annotations.SerializedName("message")
    final String message;
    @com.google.gson.annotations.SerializedName("fields")
    final String fields;

    public Status(int code, String message, String fields) {
        this.code = code;
        this.message = message;
        this.fields = fields;
    }
}
