package com.drkhannah.concerts;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.drkhannah.concerts.data.ConcertsContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by dhannah on 2/8/17.
 */

//This test class will test that our GetConcerts Task is inserting
//records into the database and purging old records

@RunWith(AndroidJUnit4.class)
public class TestGetConcertsTask {

    Context mContext = InstrumentationRegistry.getTargetContext();
    static final String TEST_ARTIST = "nofx";
    static final String TEST_ARTIST_IMAGE = "https://s3.amazonaws.com/bit-photos/large/6739767.jpeg";
    static final String TEST_ARTIST_WEBSITE = "http://www.nofxofficialwebsite.com/";

    @Before
    public void deleteAllRecords() {
        //delete all records from Artist Table
        mContext.getContentResolver().delete(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                null,
                null
        );

        //delete all records from Concert Table
        mContext.getContentResolver().delete(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                null,
                null
        );

        //SELECT * FROM artist
        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Artist table during delete", 0, cursor.getCount());
        cursor.close();

        //SELECT * FROM concert
        cursor = mContext.getContentResolver().query(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Concert table during delete", 0, cursor.getCount());
        cursor.close();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ArtistEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ConcertEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();
    }

    @Test
    public void testGetConcertsTask() {
        GetConcertsTask getConcertsTask = new GetConcertsTask(getContext());

        //insert an artist
        long artistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", artistId != -1);
    }
}
