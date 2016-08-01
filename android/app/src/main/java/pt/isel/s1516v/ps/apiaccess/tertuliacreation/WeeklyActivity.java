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
import android.widget.Spinner;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class WeeklyActivity extends Activity implements Schedule, TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = WEEKLY_RETURN_CODE;

    private final static String INSTANCE_KEY_WEEKLY = "weekly";

    private CrUiWeekly crWeekly;

    public WeeklyActivity() {
        super();
    }

    // region Activity LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_new_weekly,
                Util.IGNORE, Util.IGNORE, null, true);

        crWeekly = savedInstanceState != null ?
                    (CrUiWeekly) savedInstanceState.getParcelable(INSTANCE_KEY_WEEKLY) :
                    new CrUiWeekly(-1, -1);

        setupSpinner(this, (Spinner) findViewById(R.id.wa_weekDay),
                R.array.new_monthlyw_weekday,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.layout.simple_spinner_item,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        crWeekly.weekDayNr = position;
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
                        crWeekly.skip = position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTANCE_KEY_WEEKLY, crWeekly);
        super.onSaveInstanceState(outState);
    }

    // endregion

    // region Action Buttons

    public void onClickCreateSchedule(View view) {
        Intent intent = new Intent();
        intent.putExtra("type", WEEKLY);
        intent.putExtra("result", crWeekly);
        setResult(RESULT_SUCCESS, intent);
        finish();
    }

    public void onClickCancel(View view) {
        Log.d("trt", "Schedule creation cancelled");
        Util.longSnack(view, R.string.new_tertulia_toast_schedule_creation_cancelled);
        setResult(RESULT_FAIL);
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

    protected WeeklyActivity(Parcel in) {
        crWeekly = in.readParcelable(CrUiWeekly.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(crWeekly, flags);
    }

    public static final Creator<WeeklyActivity> CREATOR = new Parcelable.Creator<WeeklyActivity>() {
        @Override
        public WeeklyActivity createFromParcel(Parcel in) {
            return new WeeklyActivity(in);
        }

        @Override
        public WeeklyActivity[] newArray(int size) {
            return new WeeklyActivity[size];
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
