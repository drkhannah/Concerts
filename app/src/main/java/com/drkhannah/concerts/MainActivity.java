package com.drkhannah.concerts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.drkhannah.concerts.models.Concert;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetConcertsTask.GetConcertsTaskResultCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //implementation of GetConcertsTaskResultCallback.getConcertsTaskResult()
    @Override
    public void getConcertsTaskResult(List<Concert> result) {
            ConcertListFragment concertListFragment = (ConcertListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_concert_list);
            concertListFragment.getConcertTaskResultFromMainActivity(result);
    }
}
