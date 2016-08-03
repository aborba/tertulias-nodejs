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

package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaScheduleWeekly;

public class ScheduleWeeklyActivity extends ScheduleBaseActivity {

    public final static int ACTIVITY_REQUEST_CODE = WEEKLY_RETURN_CODE;

    private final static String INSTANCE_KEY_WEEKLY = "WEEKLY";

    private TertuliaScheduleWeekly schedule;

    // region Activity LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_new_weekly,
                Util.IGNORE, Util.IGNORE, null, true);

        schedule = savedInstanceState != null ?
                (TertuliaScheduleWeekly) savedInstanceState.getParcelable(INSTANCE_KEY_WEEKLY) :
                new TertuliaScheduleWeekly(0, 0);

        setupSpinner(this, (Spinner) findViewById(R.id.wa_weekDay),
                R.array.new_monthlyw_weekday,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        schedule = new TertuliaScheduleWeekly(position, schedule.skip);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        setupSpinner(this, (Spinner) findViewById(R.id.wa_skip),
                R.array.new_weekly_skip,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        schedule = new TertuliaScheduleWeekly(schedule.weekday, position);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTANCE_KEY_WEEKLY, schedule);
        super.onSaveInstanceState(outState);
    }

    // endregion

    // region Action Buttons

    public void onClickCreateSchedule(View view) {
        Intent intent = new Intent();
        intent.putExtra("result", schedule);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancel(View view) {
        Log.d("trt", "Schedule creation cancelled");
        Util.longSnack(view, R.string.new_tertulia_toast_schedule_creation_cancelled);
        setResult(RESULT_CANCELED);
        finish();
    }

    // endregion

    // region Schedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

}
