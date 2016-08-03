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

package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundle;

public class TertuliaEdition extends TertuliaBase {

    public final int id;
    public final LocationEdition location;
    public final Role role;
    public ApiLink[] links;

    public TertuliaEdition(int id, String name, String subject, boolean isPrivate,
                           Role role,
                           LocationEdition location,
                           TertuliaSchedule tSchedule,
                           String scheduleType,
                           ApiLink[] links) {
        super(name, subject, isPrivate, tSchedule, SCHEDULES.valueOf(scheduleType));
        this.id = id;
        this.location = location;
        this.role = role;
        this.links = links;
    }

    public TertuliaEdition(ApiTertuliaEdition tertulia, TertuliaSchedule schedule, ApiLink[] links) {
        super(tertulia.tr_name, tertulia.tr_subject, tertulia.tr_isPrivate, schedule, SCHEDULES.valueOf(tertulia.sc_name.toUpperCase()));
        id = Integer.parseInt(tertulia.tr_id);
        role = new Role(tertulia.ro_id, tertulia.ro_name);
        location = new LocationEdition(tertulia);
        this.links = links;
    }

    public TertuliaEdition(ApiTertuliaEdition tertulia, ApiLink[] links) {
        this(tertulia, null, links);
    }

    public TertuliaEdition(ApiTertuliaEditionBundle apiTertuliaEditionBundle) {
        this(apiTertuliaEditionBundle.tertulia, apiTertuliaEditionBundle.links);
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof TertuliaEdition))
            return false;
        TertuliaEdition other = (TertuliaEdition) obj;
        return other.id == this.id && other.name.equals(name);
    }

    // region Parcelable

    protected TertuliaEdition(Parcel in) {
        super(in);
        id = in.readInt();
        role = new Role(in);
        location = new LocationEdition(in);
        links = in.createTypedArray(ApiLink.CREATOR);
    }

    public static final Creator<TertuliaEdition> CREATOR = new Creator<TertuliaEdition>() {
        @Override
        public TertuliaEdition createFromParcel(Parcel in) {
            return new TertuliaEdition(in);
        }

        @Override
        public TertuliaEdition[] newArray(int size) {
            return new TertuliaEdition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(id);
        role.writeToParcel(out, flags);
        location.writeToParcel(out, flags);
        out.writeTypedArray(links, flags);
    }

    // endregion
}
