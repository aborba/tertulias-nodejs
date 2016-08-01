package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;

public class MonthlywActivity extends Activity implements Schedule, TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = MONTHLYW_RETURN_CODE;

    private final static String INSTANCE_KEY_MONTHLYW = "monthlyw";

    private ToggleButton fromEndVw;
    private CrUiMonthlyW crMonthlyW;

    public MonthlywActivity() {
        super();
    }

    // region Activity LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthlyw);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_new_monthlyw,
                Util.IGNORE, Util.IGNORE, null, true);

        crMonthlyW = savedInstanceState != null ?
                    (CrUiMonthlyW) savedInstanceState.getParcelable(INSTANCE_KEY_MONTHLYW) :
                    new CrUiMonthlyW(-1, -1, true, -1);

        setupSpinner(this, (Spinner) findViewById(R.id.mwa_weekDay),
                R.array.new_monthlyw_weekday,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        crMonthlyW.weekDayNr = position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        setupSpinner(this, (Spinner) findViewById(R.id.mwa_weekNr),
                R.array.new_monthlyw_weeknr,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        crMonthlyW.weekNr = position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        setupSpinner(this, (Spinner) findViewById(R.id.mwa_skip),
                R.array.new_monthly_skip,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        crMonthlyW.skip = position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        fromEndVw = (ToggleButton) findViewById(R.id.mwa_fromend);
        fromEndVw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    crMonthlyW.isFromStart = false;
                } else {
                    crMonthlyW.isFromStart = true;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTANCE_KEY_MONTHLYW, crMonthlyW);
        super.onSaveInstanceState(outState);
    }

    // endregion

    // region Action Buttons

    public void onClickCreateSchedule(View view) {
        Intent intent = new Intent();
        intent.putExtra("type", MONTHLYW);
        intent.putExtra("result", crMonthlyW);
        setResult(RESULT_SUCCESS, intent);
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

    protected MonthlywActivity(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }

    public static final Creator<MonthlywActivity> CREATOR = new Parcelable.Creator<MonthlywActivity>() {
        @Override
        public MonthlywActivity createFromParcel(Parcel in) {
            return new MonthlywActivity(in);
        }

        @Override
        public MonthlywActivity[] newArray(int size) {
            return new MonthlywActivity[size];
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

}
