package com.drkhannah.concerts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.drkhannah.concerts.data.ConcertsContract;
import com.drkhannah.concerts.data.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Vector;

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
        GetConcertsTask getConcertsTask = new GetConcertsTask(getContext(), null);

        //insert an artist
        long artistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", artistId != -1);

        // Data was inserted, now lets pull it out and look at it
        //SELECT * FROM artist
        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // check to see if we got any records back from the query
        assertTrue( "Error: No Records returned from artist query", cursor.moveToFirst() );

        //check artist name in cursor
        assertEquals("insertArtist() didn't insert the correct artist name", TEST_ARTIST, cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME)));

        //check artist image in cursor
        assertEquals("insertArtist() didn't insert the correct image", TEST_ARTIST_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE)));

        //check artist website in cursor
        assertEquals("insertArtist() didn't  insert the correct website", TEST_ARTIST_WEBSITE, cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE)));

        //check for artist
        long checkedArtistId = getConcertsTask.checkForArtist(TEST_ARTIST);

        //do artistId and checkedArtistId match
        assertEquals("artist _id doesn't match the _id that checkForArtist() returnd", artistId, checkedArtistId);

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ArtistEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();
    }

    @Test
    public void testPurgeConcerts() {
        GetConcertsTask getConcertsTask = new GetConcertsTask(getContext(), null);

        //insert an artist
        long artistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", artistId != -1);

        //create concert record
        ContentValues firstConcert = TestUtils.createConcertValues(artistId);
        //create second concert record
        ContentValues secondConcert = TestUtils.createDiffConcertValues(artistId);
        // Vector of ContentValues for concerts we will build and insert into the database
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(2);
        contentValuesVector.add(firstConcert);
        contentValuesVector.add(secondConcert);

        //convert Vector<ContentValues> into an Array
        ContentValues[] concertsArray = new ContentValues[contentValuesVector.size()];
        contentValuesVector.toArray(concertsArray);

        //bulk insert the concerts arrray
        int concertsInserted = mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, concertsArray);

        //assert that we did insert two concerts records to the Concert Table
        assertEquals(2, concertsInserted);

        //get old artist _id before inserting the same artist again
        long oldArtistId = getConcertsTask.checkForArtist(TEST_ARTIST);

        //do artistId and checkedArtistId match
        assertEquals("artist _id doesn't match the _id that checkForArtist() returned", artistId, oldArtistId);

        //insert the same artist in order to get a new artist _id
        long newArtistId = getConcertsTask.insertArtist(TEST_ARTIST, TEST_ARTIST_IMAGE, TEST_ARTIST_WEBSITE);
        //was the artist inserted correctly?
        assertTrue("Error: insertArtist returned an invalid _id on insert", newArtistId != -1);

        //create concert record with the new artist _id
        ContentValues firstConcertWithNewArtistId = TestUtils.createConcertValues(newArtistId);

        // Vector of ContentValues for new concert with new artist _id we will build and insert into the database
        Vector<ContentValues> newContentValuesVector = new Vector<ContentValues>(1);
        newContentValuesVector.add(firstConcertWithNewArtistId);

        //convert Vector<ContentValues> into an Array
        ContentValues[] newConcertsArray = new ContentValues[newContentValuesVector.size()];
        newContentValuesVector.toArray(newConcertsArray);

        //bulk insert the concerts
        int concertInserted = mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, newConcertsArray);

        //assert that we did insert 1 concert records to the Concert Table
        assertEquals(1, concertInserted);

        //purge the old concerts
        //at this point there are THREE concerts in the database for the artist
        //but TWO of them are based on the old artist _id so they need to be purged
        int concertsDeleted = getConcertsTask.purgeOldConcerts(oldArtistId);

        //the two old concerts should have been deleted
        assertEquals(2, concertsDeleted);

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ArtistEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ConcertEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();
    }
}
