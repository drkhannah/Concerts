package com.drkhannah.concerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.drkhannah.concerts.data.ConcertsContract;

import java.util.concurrent.TimeUnit;

import static com.drkhannah.concerts.R.string.extra_artist_name;


public class ConcertListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ConcertListFragment.class.getSimpleName();
    private static final int CONCERTS_LOADER_ID = 1;

    private RecyclerView mConcertsRecyclerView;
    private ConcertsRecyclerViewAdapter mConcertsRecyclerViewAdapter;
    private TextView mEmptyView;

    private BroadcastReceiver mEmptyTextViewBroadcastReceiver;

    // projection for our concert list loader
    final String[] CONCERTS_LIST_PROJECTION = new String[] {
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME,
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE,
            ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP,
            ConcertsContract.ConcertEntry.COLUMN_TTILE,
            ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME,
            ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS
    };

    public ConcertListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(CONCERTS_LOADER_ID, null, this);

        //broadcast receiver for mEmptyTextView text broadcast from ConcertsService
        mEmptyTextViewBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String emptyTextViewString = intent.getStringExtra(getString(R.string.empty_text_view_extra));
                mConcertsRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(emptyTextViewString);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        //register for mEmptyTextView local broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mEmptyTextViewBroadcastReceiver, new IntentFilter(getString(R.string.empty_text_action)));
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister from mEmptyTextView local broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mEmptyTextViewBroadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //get a handle to the RecyclerView in activity_main.xml
        mConcertsRecyclerView = (RecyclerView) rootView.findViewById(R.id.concerts_recyclerview);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

        // use a linear layout manager for the RecyclerView
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mConcertsRecyclerView.setLayoutManager(linearLayoutManager);

        // specify an adapter for the RecyclerView
        mConcertsRecyclerViewAdapter = new ConcertsRecyclerViewAdapter(getActivity(), null);
        mConcertsRecyclerView.setAdapter(mConcertsRecyclerViewAdapter);

        return rootView;
    }

    public void onArtistNameChanged() {
        getLoaderManager().restartLoader(CONCERTS_LOADER_ID, null, this);
    }

    //inflate options menu in MainActivity's Toolbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    //respond to Toolbar Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //called when a new Loader needs to be created.
        String artistName = Utils.getSharedPrefsArtistName(getActivity());
        Uri concertListForArtistUri = ConcertsContract.ConcertEntry.buildConcertListForArtistUri(artistName);
        return new CursorLoader(getActivity(), concertListForArtistUri, CONCERTS_LIST_PROJECTION, null, null, null);
    }

    public void startConcertsService() {
        //explicit intent to start the ConcertsService
        Intent concertsServiceIntent = new Intent(getActivity(), ConcertsService.class);
        concertsServiceIntent.putExtra(getString(extra_artist_name), Utils.getSharedPrefsArtistName(getActivity()));
        getActivity().startService(concertsServiceIntent);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            long currentTime = System.currentTimeMillis();
            long timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP));
            long timeDifference = currentTime - timeStamp;
            long oneDay = TimeUnit.DAYS.toMillis(1);
            if (timeDifference < oneDay) {
                mConcertsRecyclerViewAdapter.swapCursor(cursor);
                mConcertsRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            } else {
                mConcertsRecyclerViewAdapter.swapCursor(cursor);
                mConcertsRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(getString(R.string.searching_for_artist,Utils.getSharedPrefsArtistName(getActivity())));
                startConcertsService();
            }
        } else {
            mConcertsRecyclerViewAdapter.swapCursor(null);
            mConcertsRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(getString(R.string.searching_for_artist,Utils.getSharedPrefsArtistName(getActivity())));
            startConcertsService();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mConcertsRecyclerViewAdapter.swapCursor(null);
        mConcertsRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setText(getString(R.string.searching_for_artist,Utils.getSharedPrefsArtistName(getActivity())));
    }

}
