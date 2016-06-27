package pt.isel.s1516v.ps.apiaccess.support.remote;

public class Page {
    @com.google.gson.annotations.SerializedName("offset")
    final int pageOffset;
    @com.google.gson.annotations.SerializedName("size")
    final int pageSize;
    @com.google.gson.annotations.SerializedName("itemsCount")
    final int itemsCount;

    public Page(int pageOffset, int pageSize, int itemsCount) {
        this.pageOffset = pageOffset;
        this.pageSize = pageSize;
        this.itemsCount = itemsCount;
    }
}
