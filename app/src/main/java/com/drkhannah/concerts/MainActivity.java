package com.drkhannah.concerts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.drkhannah.concerts.models.Concert;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mConcertsRecyclerView;
    private ConcertsRecyclerViewAdapter mConcertsRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();

        //Check network connection before "executing" GetConcertsTask
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            GetConcertsTask getConcertsTask = new GetConcertsTask(this, mConcertsRecyclerViewAdapter);
            getConcertsTask.execute("nofx");
        } else {
            Log.e(LOG_TAG, "Not connected to network");
        }

    }

    private void setupRecyclerView() {
        //get a handle to the RecyclerView in activity_main.xml
        mConcertsRecyclerView = (RecyclerView) findViewById(R.id.concerts_recyclerview);

        // use a linear layout manager for the RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        mConcertsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter for the RecyclerView
        mConcertsRecyclerViewAdapter = new ConcertsRecyclerViewAdapter(this, new ArrayList<Concert>());
        mConcertsRecyclerView.setAdapter(mConcertsRecyclerViewAdapter);

    }
}
