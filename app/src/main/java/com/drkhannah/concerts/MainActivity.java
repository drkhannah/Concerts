package com.drkhannah.concerts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.drkhannah.concerts.models.Concert;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetConcertsTask.GetConcertsTaskResultCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mConcertsRecyclerView;
    private ConcertsRecyclerViewAdapter mConcertsRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
        getConcerts();
    }

    private void setupRecyclerView() {
        //get a handle to the RecyclerView in activity_main.xml
        mConcertsRecyclerView = (RecyclerView) findViewById(R.id.concerts_recyclerview);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        // use a linear layout manager for the RecyclerView
        mLinearLayoutManager = new LinearLayoutManager(this);
        mConcertsRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter for the RecyclerView
        mConcertsRecyclerViewAdapter = new ConcertsRecyclerViewAdapter(this);
        mConcertsRecyclerView.setAdapter(mConcertsRecyclerViewAdapter);

    }

    private void getConcerts() {
        //Check network connection before "executing" GetConcertsTask
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            GetConcertsTask getConcertsTask = new GetConcertsTask(this);
            getConcertsTask.execute("Billy Joel");
        } else {
            Log.e(LOG_TAG, "Not connected to network");
        }
    }

    //method of GetConcertsTaskResultCallback
    @Override
    public void getConcertsTaskResult(List<Concert> result) {
        if (result != null) {
            mConcertsRecyclerViewAdapter.updateData(result);
            mConcertsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mConcertsRecyclerViewAdapter.updateData(null);
            mConcertsRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

    }
}
