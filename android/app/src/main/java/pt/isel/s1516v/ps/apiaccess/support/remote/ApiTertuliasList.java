package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliasList {
    @com.google.gson.annotations.SerializedName("tertulias")
    public final ApiTertuliaListItem[] items;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiTertuliasList() {
        this.items = null;
        this.links = null;
    }
}
