package com.drkhannah.concerts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.drkhannah.concerts.models.Concert;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetConcertsTask.GetConcertsTaskResultCallback,
        ConcertsRecyclerViewAdapter.ConcertsRecyclerViewAdapterItemClick {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";

    private boolean mTwoPane;

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
    }

    @Override
    protected void onPause() {
        //remove the ConcertDetailFragment, and clear all ConcertDetailFragments from the Activity's BackStack
        FragmentManager manager = getSupportFragmentManager();
        ConcertDetailFragment concertDetailFragment = (ConcertDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
        if (concertDetailFragment != null) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            manager.beginTransaction()
                    .remove(concertDetailFragment)
                    .commit();
        }
        super.onPause();
    }

    //implementation of GetConcertsTaskResultCallback.getConcertsTaskResult()
    @Override
    public void getConcertsTaskResult(List<Concert> result) {
            ConcertListFragment concertListFragment = (ConcertListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_concert_list);
            concertListFragment.getConcertTaskResultFromMainActivity(result);
    }

    //fired when user clicks an item in the ConcertsRecyclerViewAdapter
    @Override
    public void onConcertsRecyclerViewItemClick(Concert concert) {
        if (mTwoPane) {
            //replace the ConcertDetailFragment with a new one
            ConcertDetailFragment concertDetailFragment = ConcertDetailFragment.newInstance(concert);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.concert_detail_container, concertDetailFragment, DETAIL_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        } else {
            //create an explicit Intent to start ConcertDetailActivity
            //include the Concert object in the Intent
            Intent intent = new Intent(this, ConcertDetailActivity.class);
            intent.putExtra(getString(R.string.extra_concert), concert);
            startActivity(intent);
        }
    }

    public boolean isTwoPane(){
        return mTwoPane;
    }
}
