package com.drkhannah.concerts;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ConcertsRecyclerViewAdapter.ConcertsRecyclerViewAdapterItemClick {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";
    private static final int JOB_NUMBER = 1;

    private boolean mTwoPane;
    private String mArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        //setup App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.concert_detail_container) != null;

        //Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invoke Search Dialog
                onSearchRequested();
            }
        });

        mArtist = Utils.getSharedPrefsArtistName(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob();
        } else {
            setAlarm();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        //get the service's name
        ComponentName serviceName = new ComponentName(this, ConcertsJobService.class);

        //get the job scheduler service
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        long oneDay = TimeUnit.DAYS.toMillis(1);

        JobInfo jobInfo = new JobInfo.Builder(JOB_NUMBER, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .setPersisted(true)
                .setPeriodic(SystemClock.elapsedRealtime() + 60000)
                .build();

        jobScheduler.schedule(jobInfo);
    }

    private void setAlarm() {
        //get the Alarm Service
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        //create an Intent and wrap it in a PendingIntent
        //so the AlarmManager can execute it with the Concerts app's permissions
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        long oneDay = TimeUnit.DAYS.toMillis(1);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + oneDay, AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //get the query delivered from the Search Dialog
            String artistName = intent.getStringExtra(SearchManager.QUERY);

            //save query to ArtistSuggestionsProvider
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    ArtistSuggestionsProvider.AUTHORITY, ArtistSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(artistName, null);

            //save artist name from Search Dialog to SharedPreferences
            Utils.saveSharedPrefsArtistName(getApplicationContext(), artistName);

            if (!artistName.equalsIgnoreCase(mArtist)) {
                //Restart CursorLoader in ConcertListFragment
                ConcertListFragment concertListFragment = (ConcertListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_concert_list);
                concertListFragment.onArtistNameChanged();
                mArtist = artistName;
            }
        }
    }

    @Override
    protected void onPause() {
        //remove the ConcertDetailFragment, and clear all ConcertDetailFragments from the Activity's BackStack
        ConcertDetailFragment concertDetailFragment = (ConcertDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
        if (concertDetailFragment != null) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .remove(concertDetailFragment)
                    .commit();
        }
        super.onPause();
    }

    //fired when user clicks an item in the ConcertsRecyclerViewAdapter
    @Override
    public void onConcertsRecyclerViewItemClick(Uri concertUri) {
        if (mTwoPane) {
            //replace the ConcertDetailFragment with a new one
            ConcertDetailFragment concertDetailFragment = ConcertDetailFragment.newInstance(concertUri);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.concert_detail_container, concertDetailFragment, DETAIL_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        } else {
            //create an explicit Intent to start ConcertDetailActivity
            //include the Concert object in the Intent
            Intent intent = new Intent(this, ConcertDetailActivity.class);
            intent.setData(concertUri);
            startActivity(intent);
        }
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }
}
