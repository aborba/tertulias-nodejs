package pt.isel.s1516v.ps.apiaccess.support.remote;

import java.util.HashMap;

public class ApiHome {

    @com.google.gson.annotations.SerializedName("links")
    private final ApiLink[] links;
    private HashMap<String, ApiLink> map;
    boolean isMapped = false;

    public ApiHome(ApiLink[] links) {
        this.links = links;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ApiLink link : links)
            sb.append(link.toString()).append(", ");
        return sb.substring(0, sb.length() - 2).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ApiHome)) return false;
        ApiHome other = (ApiHome) obj;
        for (ApiLink otherLink : other.links) {
            boolean isEqual = false;
            for (ApiLink link : links)
                if (otherLink.equals(link)) {
                    isEqual = true;
                    break;
                }
            if (!isEqual) return false;
        }
        return true;
    }

    public String getRoute(String tag) {
        return getLink(tag).href;
    }

    public String getMethod(String tag) {
        return getLink(tag).method;
    }

    private ApiLink getLink(String tag) {
        if (!isMapped) {
            if (map == null)
                map = new HashMap<>();
            for (ApiLink link : links)
                map.put(link.rel, link);
            isMapped = true;
        }
        if (!map.containsKey(tag))
            throw new IllegalArgumentException();
        return map.get(tag);
    }

}
