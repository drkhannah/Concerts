package com.drkhannah.concerts.data;

/**
 * Created by dhannah on 2/1/17.
 */

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestConcertsProvider {

    Context mContext = InstrumentationRegistry.getTargetContext();
    long TEST_ARTIST_ID;

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

    //test to see if provider is registered correctly
    @Test
    public void testProviderRegistry() {
        //get an instance of the PackageManager so we can use it to get
        //info for the ConcertsProvider
        PackageManager pm = mContext.getPackageManager();

        // the component name is based on the package name from the Context and the ConcertsProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(), ConcertsProvider.class.getName());

        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the ConcertsContract.
            assertEquals("Error: ConcertsProvider registered with authority: " +
                    providerInfo.authority +
                    " instead of authority: " +
                    ConcertsContract.CONTENT_AUTHORITY, ConcertsContract.CONTENT_AUTHORITY, providerInfo.authority);

        } catch (PackageManager.NameNotFoundException e) {
            // ConcertsProvider is not registered correctly.
            assertTrue("Error: ConcertsProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    //test that the ConcertsProvider returns the correct MIME type for each URI it can handle
    @Test
    public void testGetType() {
        // content://com.drkhannah.concert.provider/artist/
        String type = mContext.getContentResolver().getType(ConcertsContract.ArtistEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.drkhannah.concert.provider/artist/
        assertEquals("Error: the ArtistEntry CONTENT_URI should return ArtistEntry.CONTENT_TYPE",
                ConcertsContract.ArtistEntry.CONTENT_TYPE, type);

        // content://com.drkhannah.concert.provider/concert/
        type = mContext.getContentResolver().getType(ConcertsContract.ConcertEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.drkhannah.concert.provider/concert/
        assertEquals("Error: the ConcertEntry CONTENT_URI should return ConcertEntry.CONTENT_TYPE",
                ConcertsContract.ConcertEntry.CONTENT_TYPE, type);

        String testArtist = "nofx";
        // content://com.drkhannah.concert.provider/concert/nofx
        type = mContext.getContentResolver().getType(ConcertsContract.ConcertEntry.buildConcertListForArtistUri(testArtist));
        // vnd.android.cursor.dir/com.drkhannah.concert.provider/concert/nofx
        assertEquals("Error: the ConcertEntry CONTENT_URI should return ConcertEntry.CONTENT_TYPE",
                ConcertsContract.ConcertEntry.CONTENT_TYPE, type);

        String testDate = "Friday%2C%20March%2017%2C%202017%20at%2012%3A30PM";
        // content://com.drkhannah.concerts.provider/concert/nofx/Friday%2C%20March%2017%2C%202017%20at%2012%3A30PM
        type = mContext.getContentResolver().getType(ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(testArtist, testDate));
        // vnd.android.cursor.item/com.drkhannah.concerts.provider/concert/nofx/Friday%2C%20March%2017%2C%202017%20at%2012%3A30PM
        assertEquals("Error: the ConcertEntry CONTENT_URI should return ConcertEntry.CONTENT_TYPE",
                ConcertsContract.ConcertEntry.CONTENT_ITEM_TYPE, type);
    }

    @Test
    public void testInsertArtist() {
        //create artist record ContentValues
        ContentValues artistValues = TestUtils.createArtistValues();

        // Register a content observer for our insert().
        TestUtils.TestContentObserver artistObserver = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ConcertsContract.ArtistEntry.CONTENT_URI, true, artistObserver);

        //insert a artist record into the Artist Table
        Uri artistUri = mContext.getContentResolver().insert(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                artistValues);

        // If this fails, getContext().getContentResolver().notifyChange(uri, null); is not being called in insert() of ConcertsProvider.
        artistObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(artistObserver);

        //assert that the Uri returned from the insert is not null
        //and it is what we expect it to be
        assertNotNull("Error: Null Uri returned: ", artistUri);
        assertEquals("Error: Uri doesn't match the expected result", "content://com.drkhannah.concerts.provider/artist/1", artistUri.toString());

        //SELECT * FROM artist
        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor from the Content Provider
        TestUtils.validateCursor("testQueryListOfConcertsForArtist", cursor, artistValues);

        cursor.close();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ArtistEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();
    }

    @Test
    public void testBulkInsertConcerts() {
        // insert our artist record into the database
        SQLiteDatabase db = new ConcertsDbHelper(mContext).getWritableDatabase();

        //get test values for an artist record
        ContentValues artistValues = TestUtils.createArtistValues();
        // Insert artistValues into database and get a row ID back
        long artistRowId = db.insert(ConcertsContract.ArtistEntry.TABLE_NAME, null, artistValues);
        // Verify we got a row ID back.
        assertTrue(artistRowId != -1);

        //create concert record
        ContentValues firstConcert = TestUtils.createConcertValues(artistRowId);
        //create second concert record
        ContentValues secondConcert = TestUtils.createDiffConcertValues(artistRowId);
        //create array of ContentValues that holds the first and second concert records
        ContentValues[] bulkConcertValues = {firstConcert, secondConcert};

        // Register a content observer for our bulk insert.
        TestUtils.TestContentObserver concertObserver = TestUtils.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ConcertsContract.ConcertEntry.CONTENT_URI, true, concertObserver);

        //bulk insert the concerts
        int concertsInserted = mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, bulkConcertValues);

        // If this fails, getContext().getContentResolver().notifyChange(uri, null); is not being called in BulkInsert() of ConcertsProvider.
        concertObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(concertObserver);

        //assert that we did insert two concerts records to the Concert Table
        assertEquals(2, concertsInserted);

        // SELECT * FROM concert
        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        // we should have as many records in the database as we've inserted
        assertEquals(2, cursor.getCount());
        cursor.close();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ConcertEntry.CONTENT_URI)
                .getLocalContentProvider()
                .shutdown();
    }


    //test querying for a list of concerts for a given artist
    @Test
    public void testQueryListOfConcertsForArtist() {
        // insert our artist record into the database
        SQLiteDatabase db = new ConcertsDbHelper(mContext).getWritableDatabase();

        //get test values for an artist record
        ContentValues artistValues = TestUtils.createArtistValues();
        // Insert artistValues into database and get a row ID back
        long artistRowId = db.insert(ConcertsContract.ArtistEntry.TABLE_NAME, null, artistValues);
        // Verify we got a row ID back.
        assertTrue(artistRowId != -1);

        // Create concert values
        ContentValues concertValues = TestUtils.createConcertValues(artistRowId);

        // Insert concert ContentValues into database and get a row ID back
        long concertRowId = db.insert(ConcertsContract.ConcertEntry.TABLE_NAME, null, concertValues);
        assertTrue(concertRowId != -1);

        // Create a different set of concert values
        ContentValues diffConcertValues = TestUtils.createDiffConcertValues(artistRowId);

        // Insert concert values with different date to test it doesn't replace
        // the single record we have in the concert table already
        concertRowId = db.insert(ConcertsContract.ConcertEntry.TABLE_NAME, null, diffConcertValues);
        assertTrue(concertRowId != -1);

        //close the database
        db.close();

        //query the CONCERT_LIST_FOR_ARTIST Uri of ConcertsProvider using a ContentResolver
        String artistName = artistValues.getAsString(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME);
        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ConcertEntry.buildConcertListForArtistUri(artistName),
                null,
                null,
                null,
                null);

        // we should have as many records in the database as we've inserted
        assertEquals(2, cursor.getCount());

        cursor.close();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ConcertEntry.buildConcertListForArtistUri(artistName))
                .getLocalContentProvider()
                .shutdown();
    }

    @Test
    public void testQueryConcertForDate() {
        // insert our artist record into the database
        SQLiteDatabase db = new ConcertsDbHelper(mContext).getWritableDatabase();

        //get test values for an artist record
        ContentValues artistValues = TestUtils.createArtistValues();
        // Insert artistValues into database and get a row ID back
        long artistRowId = db.insert(ConcertsContract.ArtistEntry.TABLE_NAME, null, artistValues);
        // Verify we got a row ID back.
        assertTrue(artistRowId != -1);

        // Create concert values
        ContentValues concertValues = TestUtils.createConcertValues(artistRowId);

        // Insert concert ContentValues into database and get a row ID back
        long concertRowId = db.insert(ConcertsContract.ConcertEntry.TABLE_NAME, null, concertValues);
        assertTrue(concertRowId != -1);

        // Create a different set of concert values
        ContentValues diffConcertValues = TestUtils.createDiffConcertValues(artistRowId);

        // Insert concert values with different date to test it doesn't replace
        // the single record we have in the concert table already
        concertRowId = db.insert(ConcertsContract.ConcertEntry.TABLE_NAME, null, diffConcertValues);
        assertTrue(concertRowId != -1);

        //close the database
        db.close();

        //query the CONCERT_FOR_DATE Uri of ConcertsProvider using a ContentResolver
        String artistName = artistValues.getAsString(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME);
        String concertDate = concertValues.getAsString(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME);
        Cursor cursor = mContext.getContentResolver().query(
                ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(artistName, concertDate),
                null,
                null,
                null,
                null);

        // Make sure we got back the first concertValues we inserted
        TestUtils.validateCursor("testQueryListOfConcertsForArtist", cursor, concertValues);

        cursor.close();

        mContext.getContentResolver()
                .acquireContentProviderClient(ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(artistName, concertDate))
                .getLocalContentProvider()
                .shutdown();
    }
}
