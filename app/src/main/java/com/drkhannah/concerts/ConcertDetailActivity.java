package com.drkhannah.concerts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

        if (savedInstanceState == null) {
            // Create the ConcertDetailFragment
            // pass Concert object as an argument to the ConcertDetailFragment
            Bundle arguments = new Bundle();
            arguments.putParcelable(getString(R.string.extra_concert), concert);
            ConcertDetailFragment fragment = ConcertDetailFragment.newInstance(concert);

            //add it to the activity using FragmentManager
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.concert_detail_container, fragment)
                    .commit();
        }
    }
}
