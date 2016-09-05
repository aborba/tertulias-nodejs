package pt.isel.pdm.g04.pf.presentation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Preferences;
import pt.isel.pdm.g04.pf.presentation.widget.SliderPreference;


public class PreferencesActivity extends Activity {

    //region Log Methods

    private static final String TAG = PreferencesActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("[" + TAG + "] onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("[" + TAG + "] onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("[" + TAG + "] onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("[" + TAG + "]onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("[" + TAG + "] onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("[" + TAG + "] onDestroy");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Logger.d("[" + TAG + "] onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.d("[" + TAG + "] onSaveInstanceState");
    }

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        new MainSettingsFragment()).commit();
    }

    public static class MainSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SliderPreference mMemoryCache, mDiskCacheSlider;
        private int mDefaultSize;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            LinearLayout root = (LinearLayout) getActivity().findViewById(android.R.id.content).getParent();
            Toolbar bar = (Toolbar) LayoutInflater.from(getActivity()).inflate(R.layout.activity_settings, root, false);
            root.addView(bar, 0); // insert at top
            bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            // Get widgets :
            mDefaultSize = Preferences.getDefaultCacheSize();
            mMemoryCache = loadSlider(Constants.Preferences.MEMORY_CACHE_SIZE);
            mDiskCacheSlider = loadSlider(Constants.Preferences.DISK_CACHE_SIZE);

            // Set listener :
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        private SliderPreference loadSlider(String key) {
            SliderPreference sliderPreference = (SliderPreference) this.findPreference(key);
            sliderPreference.setDefault(mDefaultSize);
            setSummary(key, sliderPreference);
            sliderPreference.setMax(mDefaultSize);
            return sliderPreference;
        }

        private void setSummary(String key, SliderPreference sliderPreference) {
            Activity a = this.getActivity();
            if (a == null)
                return;

            int radius = PreferenceManager.getDefaultSharedPreferences(a).getInt(key, mDefaultSize);
            sliderPreference.setSummary(this.getString(R.string.slider_summary).replace("$1", "" + radius));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Set seekbar summary :
            setSummary(Constants.Preferences.DISK_CACHE_SIZE, mDiskCacheSlider);
            setSummary(Constants.Preferences.MEMORY_CACHE_SIZE, mMemoryCache);
        }
    }
}
