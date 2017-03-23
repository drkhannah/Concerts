package com.drkhannah.concerts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.concurrent.TimeUnit;

import static com.drkhannah.concerts.sync.ConcertsSyncAdapter.configurePeriodicSync;

/**
 * Created by dhannah on 3/23/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpAppBar();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    private void setUpAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    //respond to Toolbar Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //preference fragment
    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String KEY_PREF_SYNC_INT = "sync_interval";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

//        // Registers a shared preference change listener that gets notified when preferences change
//        @Override
//        public void onResume() {
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            sp.registerOnSharedPreferenceChangeListener(this);
//            super.onResume();
//        }
//
//        // Unregisters a shared preference change listener
//        @Override
//        public void onPause() {
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            sp.unregisterOnSharedPreferenceChangeListener(this);
//            super.onPause();
//        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_SYNC_INT)) {
                Preference syncPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                syncPref.setSummary("Sync with server every " + sharedPreferences.getString(key, "") + " days");

                long syncInterval = TimeUnit.DAYS.toSeconds(Long.parseLong(sharedPreferences.getString(key, "1")));
                long flexTime = TimeUnit.HOURS.toSeconds(1);

                //configure periodic sync
                configurePeriodicSync(getActivity(), syncInterval, flexTime);
            }
        }
    }

}
