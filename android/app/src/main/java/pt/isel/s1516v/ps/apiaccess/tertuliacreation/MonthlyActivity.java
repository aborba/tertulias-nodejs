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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;

public class MonthlyActivity extends Activity implements Schedule, TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = MONTHLY_RETURN_CODE;

    private final static String INSTANCE_KEY_MONTHLY = "monthly";

    private EditText dayVw;
    private ToggleButton fromEndVw;
    private CrUiMonthly crMonthly;

    private MonthlyActivity() {
        super();
    }

    // region Activity LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_new_monthly,
                Util.IGNORE, Util.IGNORE, null, true);

        crMonthly = savedInstanceState != null ?
                    (CrUiMonthly) savedInstanceState.getParcelable(INSTANCE_KEY_MONTHLY) :
                    new CrUiMonthly(-1, true, -1);

        dayVw = (EditText) findViewById(R.id.ma_day);
        dayVw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String sValue = s.toString();
                if (TextUtils.isEmpty(sValue))
                    return;
                int value = Integer.parseInt(sValue);
                if (value == 0 || value > 31) {
                    refocusOnDayVw();
                    return;
                }
                crMonthly.dayNr = value;
            }
        });

        if (crMonthly.dayNr != -1)
            dayVw.setText(String.valueOf(crMonthly.dayNr));

        setupSpinner(this, (Spinner) findViewById(R.id.ma_skip),
                R.array.new_monthly_skip,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        crMonthly.skip = position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        fromEndVw = (ToggleButton) findViewById(R.id.ma_fromend);
        fromEndVw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crMonthly.isFromStart = !isChecked;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String dayNr = dayVw.getText().toString();
        crMonthly.dayNr = Integer.parseInt(dayNr);
        outState.putParcelable(INSTANCE_KEY_MONTHLY, crMonthly);
        super.onSaveInstanceState(outState);
    }

    // endregion

    // region Action Buttons

    public void onClickCreateSchedule(View view) {
        if(TextUtils.isEmpty(dayVw.getText().toString())) {
            refocusOnDayVw();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("type", MONTHLY);
        intent.putExtra("result", crMonthly);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancel(View view) {
        Log.d("trt", "Schedule creation cancelled");
        Util.longSnack(view, R.string.new_tertulia_toast_schedule_creation_cancelled);
        finish();
    }

    // endregion

    // region Schedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected MonthlyActivity(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }

    public static final Creator<MonthlyActivity> CREATOR = new Parcelable.Creator<MonthlyActivity>() {
        @Override
        public MonthlyActivity createFromParcel(Parcel in) {
            return new MonthlyActivity(in);
        }

        @Override
        public MonthlyActivity[] newArray(int size) {
            return new MonthlyActivity[size];
        }
    };
    // endregion

    // region Private Static Methods

    private static ArrayAdapter<CharSequence> prepareArrayAdapter(Context ctx, int source, int schema, int item) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx, source, item);
        adapter.setDropDownViewResource(schema);
        return adapter;
    }

    private static void setupSpinner(Context ctx, Spinner spinner, int source, int schema, int item, AdapterView.OnItemSelectedListener listener) {
        spinner.setAdapter(prepareArrayAdapter(ctx, source, schema, item));
        spinner.setOnItemSelectedListener(listener);
    }

    // endregion

    // region Private Methods

    private void refocusOnDayVw() {
        Util.longSnack(findViewById(android.R.id.content), R.string.new_monthly_verify_day_toast);
        dayVw.requestFocus();
    }

    // endregion

}
