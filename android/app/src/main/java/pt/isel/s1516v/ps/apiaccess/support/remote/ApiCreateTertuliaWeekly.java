package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiCreateTertuliaWeekly extends ApiCreateTertulia {

    @com.google.gson.annotations.SerializedName("weekDay")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiCreateTertuliaWeekly(String name, String subject,
                                   String location, String address, String zip, String city, String country,
                                   String latitude, String longitude,
                                   String weekDay, int skip,
                                   boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
                "Weekly",
                isPrivate);
        this.weekDay = weekDay;
        this.skip = skip;
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiCreateTertuliaWeekly other = (ApiCreateTertuliaWeekly) obj;
        return other.name.equals(name) && other.subject.equals(this.subject);
    }

}
