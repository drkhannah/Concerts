package com.drkhannah.concerts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.drkhannah.concerts.models.Concert;

/**
 * Created by dhannah on 11/21/16.
 */

public class ConcertDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_detail);

        //get the Intent and its Extras that started this Activity
        Intent receivedIntent = getIntent();
        Concert concert = (Concert) receivedIntent.getParcelableExtra(getString(R.string.extra_concert));

        setUpAppBar(concert);

        if (savedInstanceState == null) {
            //if savedInstanceState is null,
            // then this activity isn't coming back from a pause state,
            // its being created for the first time

            // Create the ConcertDetailFragment
            // pass Concert object to  ConcertDetailFragment.newInstance() to be set as a fragment argument
            ConcertDetailFragment fragment = ConcertDetailFragment.newInstance(concert);

            //add it to the activity using FragmentManager
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.concert_detail_container, fragment)
                    .commit();
        }
    }

    private void setUpAppBar(Concert concert) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(concert.getArtistName());
    }
}
