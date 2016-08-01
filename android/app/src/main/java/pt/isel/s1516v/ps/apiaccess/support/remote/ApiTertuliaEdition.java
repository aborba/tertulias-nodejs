package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaEdition extends ApiTertuliaCreation {

    @com.google.gson.annotations.SerializedName("tertulia_id")
    public final String tr_id;
    @com.google.gson.annotations.SerializedName("location_id")
    public final String lo_id;
    @com.google.gson.annotations.SerializedName("schedule_id")
    public final String sc_id;
    @com.google.gson.annotations.SerializedName("schedule_description")
    public final String sc_description;
    @com.google.gson.annotations.SerializedName("role_id")
    public final String ro_id;
    @com.google.gson.annotations.SerializedName("role_name")
    public final String ro_name;
    @com.google.gson.annotations.SerializedName("messages")
    public final int messagesCount;

    public ApiTertuliaEdition(String tr_id, String tr_name, String tr_subject, boolean tr_isPrivate, // Tertulia
                              String lo_id, String lo_name, String lo_address, String lo_zip, String lo_city, String lo_country, // location
                              String lo_latitude, String lo_longitude,
                              String sc_id, String sc_name, String sc_description, // schedule
                              String ro_id, String ro_name, // role
                              int messagesCount) {
        super(tr_name, tr_subject, tr_isPrivate, // Tertulia
                lo_name, lo_address, lo_zip, lo_city, lo_country, // location
                lo_latitude, lo_longitude,
                sc_name); // schedule
        this.tr_id = tr_id;
        this.lo_id = lo_id;
        this.sc_id = sc_id; this.sc_description = sc_description;
        this.ro_id = ro_id; this.ro_name = ro_name;
        this.messagesCount = messagesCount;
    }

    // region ApiTertuliaCreation

    @Override
    protected String toStringContribution() {
        return null;
    }

    // endregion

    @Override
    public String toString() { return tr_name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaEdition other = (ApiTertuliaEdition) obj;
        return obj instanceof ApiTertuliaEdition && other.tr_id == this.tr_id && other.tr_name.equals(this.tr_name);
    }

}
