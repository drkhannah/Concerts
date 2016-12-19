package com.drkhannah.concerts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.drkhannah.concerts.models.Concert;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetConcertsTask.GetConcertsTaskResultCallback,
        ConcertsRecyclerViewAdapter.ConcertsRecyclerViewAdatperItemClick {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static boolean sTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.concert_detail_container) != null) {
            sTwoPane = true;
        } else {
            sTwoPane = false;
        }
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
        if (sTwoPane) {
            ConcertDetailFragment concertDetailFragment = ConcertDetailFragment.newInstance(concert);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.concert_detail_container, concertDetailFragment)
                    .commit();
        }

    }

    public static boolean isTwoPane(){
        return sTwoPane;
    }
}
