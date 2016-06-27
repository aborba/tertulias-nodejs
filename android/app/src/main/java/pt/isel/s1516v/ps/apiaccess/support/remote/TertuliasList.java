package pt.isel.s1516v.ps.apiaccess.support.remote;

public class TertuliasList {
    @com.google.gson.annotations.SerializedName("tertulias")
    final TertuliaItem[] tertuliaItems;
    @com.google.gson.annotations.SerializedName("page")
    final Page page;
    @com.google.gson.annotations.SerializedName("links")
    final LinksTertuliasGet links;
    @com.google.gson.annotations.SerializedName("status")
    final Status status;

    public TertuliasList(TertuliaItem[] tertuliaItems, Page page, LinksTertuliasGet links, Status status) {
        this.tertuliaItems = tertuliaItems;
        this.page = page;
        this.links = links;
        this.status = status;
    }
}
