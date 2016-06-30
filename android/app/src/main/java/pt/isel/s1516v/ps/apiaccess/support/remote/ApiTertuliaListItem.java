package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaListItem {

    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("name")
    public String name;
    @com.google.gson.annotations.SerializedName("subject")
    public String subject;
    @com.google.gson.annotations.SerializedName("nextEventDate")
    public String eventDate;
    @com.google.gson.annotations.SerializedName("nextEventLocation")
    public String eventLocation;
    @com.google.gson.annotations.SerializedName("messages")
    public int messagesCount;
    @com.google.gson.annotations.SerializedName("role")
    public String role;
    @com.google.gson.annotations.SerializedName("links")
    public ApiLink[] links;

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaListItem other = (ApiTertuliaListItem) obj;
        return obj instanceof ApiTertuliaListItem && other.id == this.id && other.name == this.name;
    }

}
