package com.drkhannah.concerts;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static com.drkhannah.concerts.sync.ConcertsSyncAdapter.initSyncAdapter;
import static com.drkhannah.concerts.sync.ConcertsSyncAdapter.syncNow;

public class MainActivity extends AppCompatActivity implements ConcertsRecyclerViewAdapter.ConcertsRecyclerViewAdapterItemClick {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;

    private boolean mTwoPane;
    private String mArtist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //switch theme from SplashTheme to AppTheme
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        //setup App Bar
        setupToolbar();

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

        //initialize the ConcertsSyncAdapter
        initSyncAdapter(this);

//        checkPlayServices();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    //check if Google Play Services is installed on the device
    //if not, prompt the user to do so
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

            //perform com.drkhannah.concerts.sync
            syncNow(this);

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
        popDetailsOffBackStack();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        popDetailsOffBackStack();
        ConcertListFragment concertListFragment = (ConcertListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_concert_list);
        concertListFragment.onBackPressed();
        super.onBackPressed();
    }

    void popDetailsOffBackStack() {
        //remove the ConcertDetailFragment, and clear all ConcertDetailFragments from the Activity's BackStack
        ConcertDetailFragment concertDetailFragment = (ConcertDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
        if (concertDetailFragment != null) {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction()
                    .remove(concertDetailFragment)
                    .commit();
        }
    }

    //fired when user clicks an item in the ConcertsRecyclerViewAdapter
    @Override
    public void onConcertsRecyclerViewItemClick(Uri concertUri, ConcertsRecyclerViewAdapter.ViewHolder viewHolder) {
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
            ActivityOptionsCompat activityOptions;

            RecyclerView concertsList = (RecyclerView) findViewById(R.id.concerts_recyclerview);
            View firstItemView = concertsList.getLayoutManager().findViewByPosition(0);
            if (firstItemView != null) {
                ImageView artistImageView = (ImageView) firstItemView.findViewById(R.id.artist_image);
                if (artistImageView != null) {
                    // create the transition animation - ticket icon images in the layouts
                    // of both activities are defined with android:transitionName="@string/ticket_icon_transition_name"
                    // artist images are defined with android:transitionName="@string/artist_image_transition_name"
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(viewHolder.mTicketsIconImageView, getString(R.string.ticket_icon_transition_name)),
                            new Pair<View, String>(artistImageView, getString(R.string.artist_image_transition_name)));
                } else {
                    //only animate the ticket icon
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(viewHolder.mTicketsIconImageView, getString(R.string.ticket_icon_transition_name)));
                }

            } else {
                //only animate the ticket icon
                activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        new Pair<View, String>(viewHolder.mTicketsIconImageView, getString(R.string.ticket_icon_transition_name)));
            }
            //set data of Intent
            intent.setData(concertUri);

            // start the new activity
            startActivity(intent, activityOptions.toBundle());
        }
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }
}
