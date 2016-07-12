package pt.isel.s1516v.ps.apiaccess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class NewLocationActivity extends AppCompatActivity {

    public static final String DATA_NAME = "NewLocation_Name";
    public static final String DATA_ADDRESS = "NewLocation_Address";
    public static final String DATA_ZIP = "NewLocation_Zip";
    public static final String DATA_COUNTRY = "NewLocation_Country";
    public static final String DATA_LATITUDE = "NewLocation_Latitude";
    public static final String DATA_LONGITUDE = "NewLocation_Longitude";

    private TextView nameView, addressView, zipView, countryView, latitudeView, longitudeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        Util.setupActionBar(this, R.string.title_activity_new_location, true);

        nameView = (TextView) findViewById(R.id.nt_LocationName);
        addressView = (TextView) findViewById(R.id.nt_Address);
        zipView = (TextView) findViewById(R.id.nt_Zip);
        countryView = (TextView) findViewById(R.id.nt_Country);
        latitudeView = (TextView) findViewById(R.id.nt_Latitude);
        longitudeView = (TextView) findViewById(R.id.nt_Longitude);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(DATA_NAME, nameView.getText().toString());
        outState.putString(DATA_ADDRESS, addressView.getText().toString());
        outState.putString(DATA_ZIP, zipView.getText().toString());
        outState.putString(DATA_COUNTRY, countryView.getText().toString());
        outState.putString(DATA_LATITUDE, latitudeView.getText().toString());
        outState.putString(DATA_LONGITUDE, longitudeView.getText().toString());
    }

    // region Private Methods

    private void restoreInstanceState(Bundle savedInstanceState) {
        restoreTextViewState(savedInstanceState, nameView, DATA_NAME);
        restoreTextViewState(savedInstanceState, addressView, DATA_ADDRESS);
        restoreTextViewState(savedInstanceState, zipView, DATA_ZIP);
        restoreTextViewState(savedInstanceState, countryView, DATA_COUNTRY);
        restoreTextViewState(savedInstanceState, latitudeView, DATA_LATITUDE);
        restoreTextViewState(savedInstanceState, longitudeView, DATA_LONGITUDE);
    }

    // endregion

    // region Private Static Methods

    private static void restoreTextViewState(Bundle savedInstanceState, TextView textView, String key) {
        if (savedInstanceState.containsKey(key)) textView.setText(savedInstanceState.getString(key));
    }

    // endregion

}
