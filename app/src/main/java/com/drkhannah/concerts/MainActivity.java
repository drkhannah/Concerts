package com.drkhannah.concerts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements ConcertsRecyclerViewAdapter.ConcertsRecyclerViewAdapterItemClick {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";

    private static final int SEARCH_ARTIST_REQUEST_CODE = 1;

    private boolean mTwoPane;
    private String mArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.concert_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        //Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start ArtistSearchActivity to get a result
                Intent intent = new Intent(MainActivity.this, ArtistSearchActivity.class);
                startActivityForResult(intent, SEARCH_ARTIST_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_ARTIST_REQUEST_CODE &&
                resultCode == RESULT_OK) {
            String artistName = data.getStringExtra(getString(R.string.artist_to_search)).toLowerCase();
            Utils.saveSharedPrefsArtistName(this, artistName);
            // update the artist in our second pane using the fragment manager
            if (!artistName.equals(mArtist)) {
                ConcertListFragment concertListFragment = (ConcertListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_concert_list);
                //will not be null if we are in TwoPane mode
                if (concertListFragment != null) {
                    concertListFragment.onArtistNameChanged();
                }
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
