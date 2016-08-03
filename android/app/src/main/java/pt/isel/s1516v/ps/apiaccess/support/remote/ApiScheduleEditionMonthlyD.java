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

public class ApiScheduleEditionMonthlyD {

    @com.google.gson.annotations.SerializedName("schedule_id")
    public final int sc_id;
    @com.google.gson.annotations.SerializedName("schedule_daynr")
    public final int sc_daynr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean sc_isfromstart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int sc_skip;

    public ApiScheduleEditionMonthlyD(int sc_id, int sc_daynr, boolean sc_isfromstart, int sc_skip) {
        this.sc_id = sc_id;
        this.sc_daynr = sc_daynr;
        this.sc_isfromstart = sc_isfromstart;
        this.sc_skip = sc_skip;
    }
}
