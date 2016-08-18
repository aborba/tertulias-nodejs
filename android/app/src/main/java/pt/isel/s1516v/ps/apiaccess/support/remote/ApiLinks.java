/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class ApiLinks implements Parcelable {

    @com.google.gson.annotations.SerializedName("links")
    private ApiLink[] links;
    private HashMap<String, ApiLink> map;
    boolean isMapped = false;

    public ApiLinks(ApiLink[] links) {
        this.links = links;
    }

    public boolean isEmpty() {
        return links == null || links.length == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ApiLink link : links)
            sb.append(link.toString()).append(", ");
        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ApiLinks)) return false;
        ApiLinks other = (ApiLinks) obj;
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

    public ApiLink[] get() {
        return links;
    }

    public void swap(ApiLink[] links) {
        this.links = links;
        isMapped = false;
    }

    public void swap(ApiLinks links) {
        this.links = links.links;
        isMapped = false;
    }

    public String getRoute(String tag) {
        ApiLink link = getLink(tag);
        return link == null ? null : link.href;
    }

    public String getMethod(String tag) {
        ApiLink link = getLink(tag);
        return link == null ? null : link.method;
    }

    private ApiLink getLink(String tag) {
        if (!isMapped) {
            if (links == null)
                return null;
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

    // region Parcelable

    protected ApiLinks(Parcel in) {
        links = in.createTypedArray(ApiLink.CREATOR);
        isMapped = false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(links, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApiLinks> CREATOR = new Creator<ApiLinks>() {
        @Override
        public ApiLinks createFromParcel(Parcel in) {
            return new ApiLinks(in);
        }

        @Override
        public ApiLinks[] newArray(int size) {
            return new ApiLinks[size];
        }
    };

    // endregion
}
