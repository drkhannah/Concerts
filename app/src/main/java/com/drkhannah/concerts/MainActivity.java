package com.drkhannah.concerts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
import com.drkhannah.concerts.models.Concert;

public class MainActivity extends AppCompatActivity implements ConcertsRecyclerViewAdapter.ConcertsRecyclerViewAdapterItemClick {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";

    private static final int SEARCH_ARTIST_REQUEST_CODE = 1;

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

        //create a example database to view with adb and android monitor
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("sqlite3-test.db", MODE_PRIVATE, null);
        /*
            uncomment DROP TABLE statement below only if you are running this code more than once
            to avoid error that the contacts table already exists when trying to create it a second time
        */
        //sqLiteDatabase.execSQL("DROP TABLE contacts");
        sqLiteDatabase.execSQL("CREATE TABLE contacts(name TEXT, phone INTEGER, email TEXT)");
        sqLiteDatabase.execSQL("INSERT INTO contacts VALUES('derek',1234567,'dhannah@thesoftwareguild.com')");
        sqLiteDatabase.execSQL("INSERT INTO contacts VALUES('mike',7654321,'mike@thesoftwareguild.com')");
        sqLiteDatabase.execSQL("INSERT INTO contacts VALUES('jim',0987654,'jim@thesoftwareguild.com')");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_ARTIST_REQUEST_CODE &&
                resultCode == RESULT_OK) {
            //Use SharedPreferences.Editor to save artist name to the
            //com.drkhannah.concerts.CONCERTS_SHARED_PREFERENCE_FILE Shared Preferences file
            String artistToSearch = data.getStringExtra(getString(R.string.artist_to_search)).toLowerCase();
            SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putString(getString(R.string.shared_prefs_artist_name), artistToSearch);
            sharedPrefsEditor.commit();
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

    public boolean isTwoPane() {
        return mTwoPane;
    }
}
