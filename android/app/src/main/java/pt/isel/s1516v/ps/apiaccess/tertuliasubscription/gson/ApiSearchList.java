package pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class ApiSearchList {
    @com.google.gson.annotations.SerializedName("tertulias")
    public final ApiSearchListItem[] items;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiSearchList() {
        this.items = null;
        this.links = null;
    }
}
