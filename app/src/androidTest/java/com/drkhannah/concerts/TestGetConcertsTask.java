package com.drkhannah.concerts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.drkhannah.concerts.data.ConcertsContract;
import com.drkhannah.concerts.data.TestUtils;

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
    public void testInsertArtistAndCheckForArtist() {
        GetConcertsTask getConcertsTask = new GetConcertsTask(getContext());

        //insert an artist
        long artistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", artistId != -1);

        //check for artist
        long checkedArtistId = getConcertsTask.checkForArtist(TEST_ARTIST);

        //do artistId and checkedArtistId match
        assertEquals("artist _id doesn't match the _id that checkForArtist() returnd", artistId, checkedArtistId);
    }

    @Test
    public void testPurgeConcerts() {
        GetConcertsTask getConcertsTask = new GetConcertsTask(getContext());

        //insert an artist
        long artistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", artistId != -1);

        //create concert record
        ContentValues firstConcert = TestUtils.createConcertValues(artistId);
        //create second concert record
        ContentValues secondConcert = TestUtils.createDiffConcertValues(artistId);
        //create array of ContentValues that holds the first and second concert records
        ContentValues[] bulkConcertValues = {firstConcert, secondConcert};

        //bulk insert the concerts
        int concertsInserted = mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, bulkConcertValues);

        //assert that we did insert two concerts records to the Concert Table
        assertEquals(2, concertsInserted);

        //get old artist _id before inserting the same artist again
        long oldArtistId = getConcertsTask.checkForArtist(TEST_ARTIST);

        //do artistId and checkedArtistId match
        assertEquals("artist _id doesn't match the _id that checkForArtist() returnd", artistId, oldArtistId);

        //insert the same artist in order to get a new artist _id
        long newArtistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", newArtistId != -1);

        //create concert record with the new artist _id
        ContentValues firstConcertWithNewArtistId = TestUtils.createConcertValues(newArtistId);
        //bulkInsert just this one record so that the secondConcert for this artist
        //will still have the old artist _id and will later be purged
        ContentValues[] newbulkConcertValues = {firstConcertWithNewArtistId};

        //bulk insert the concerts
        int concertInserted = mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, newbulkConcertValues);

        //assert that we did insert two concerts records to the Concert Table
        assertEquals(1, concertInserted);

        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        //purge the now old secondConcert
        int concertsDeleted = getConcertsTask.purgeOldConcerts(oldArtistId);

        //only one concert should have been deleted
        assertEquals(1, concertsDeleted);
    }
}
