package pt.isel.s1516v.ps.apiaccess.support.remote;

public class LinksTertuliasGet {
    @com.google.gson.annotations.SerializedName("self")
    final ApiSelfLink apiSelfLink;
    @com.google.gson.annotations.SerializedName("add")
    final ApiLink add;
    @com.google.gson.annotations.SerializedName("subscribe")
    final ApiLink subscribe;
    @com.google.gson.annotations.SerializedName("nextPAge")
    final ApiLink nextPage;
    @com.google.gson.annotations.SerializedName("previousPage")
    final ApiLink previousPage;

    public LinksTertuliasGet(ApiSelfLink apiSelfLink, ApiLink add, ApiLink subscribe, ApiLink nextPage, ApiLink previousPage) {
        this.apiSelfLink = apiSelfLink;
        this.add = add;
        this.subscribe = subscribe;
        this.nextPage = nextPage;
        this.previousPage = previousPage;
    }
}
