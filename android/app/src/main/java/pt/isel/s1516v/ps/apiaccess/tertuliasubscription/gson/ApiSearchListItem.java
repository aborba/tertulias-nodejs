package pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class ApiSearchListItem {

    @com.google.gson.annotations.SerializedName("id")
    final public String id;
    @com.google.gson.annotations.SerializedName("name")
    final public String name;
    @com.google.gson.annotations.SerializedName("subject")
    final public String subject;
    @com.google.gson.annotations.SerializedName("location")
    final public String location;
    @com.google.gson.annotations.SerializedName("schedule")
    final public String schedule;
    @com.google.gson.annotations.SerializedName("links")
    final public ApiLink[] links;

    public ApiSearchListItem() {
        id = name = subject = location = schedule = null;
        links = null;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiSearchListItem other = (ApiSearchListItem) obj;
        return obj instanceof ApiSearchListItem && other.id == this.id && other.name == this.name;
    }

}
