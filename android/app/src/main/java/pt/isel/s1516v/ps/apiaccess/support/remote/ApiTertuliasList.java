package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliasList {
    @com.google.gson.annotations.SerializedName("tertulias")
    public final ApiTertuliaListItem[] apiTertuliaListItems;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiTertuliasList(ApiTertuliaListItem[] apiTertuliaListItems, ApiLink[] links) {
        this.apiTertuliaListItems = apiTertuliaListItems;
        this.links = links;
    }
}
