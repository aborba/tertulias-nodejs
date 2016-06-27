package pt.isel.s1516v.ps.apiaccess.support.remote;

public class LinksTertuliasGet {
    @com.google.gson.annotations.SerializedName("self")
    final SelfLink selfLink;
    @com.google.gson.annotations.SerializedName("add")
    final Links add;
    @com.google.gson.annotations.SerializedName("subscribe")
    final Links subscribe;
    @com.google.gson.annotations.SerializedName("nextPAge")
    final Links nextPage;
    @com.google.gson.annotations.SerializedName("previousPage")
    final Links previousPage;

    public LinksTertuliasGet(SelfLink selfLink, Links add, Links subscribe, Links nextPage, Links previousPage) {
        this.selfLink = selfLink;
        this.add = add;
        this.subscribe = subscribe;
        this.nextPage = nextPage;
        this.previousPage = previousPage;
    }
}
