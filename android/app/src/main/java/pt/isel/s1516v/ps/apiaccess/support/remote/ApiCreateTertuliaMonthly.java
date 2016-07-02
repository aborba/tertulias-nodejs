package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiCreateTertuliaMonthly extends ApiCreateTertulia {

    @com.google.gson.annotations.SerializedName("dayNr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiCreateTertuliaMonthly(String name, String subject,
                                    String location, String address, String zip, String city, String country,
                                    String latitude, String longitude,
                                    int dayNr, boolean fromStart, int skip,
                                    boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
                "Monthly",
                isPrivate);
        this.dayNr = dayNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiCreateTertuliaMonthly other = (ApiCreateTertuliaMonthly) obj;
        return other.name.equals(name) && other.subject.equals(this.subject);
    }

}
