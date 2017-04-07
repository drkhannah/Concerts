package com.drkhannah.concerts.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by dhannah on 1/24/17.
 */

public class TestUtils {

    static final String TEST_ARTIST = "nofx";
    static final String TEST_ARTIST_IMAGE = "https://s3.amazonaws.com/bit-photos/large/6739767.jpeg";
    static final String TEST_ARTIST_WEBSITE = "http://www.nofxofficialwebsite.com/";
    static final String TEST_TIME_STAMP = "2009-10-02 16:52:30";

    static final String TEST_TITLE = "NOFX @ OC Fair & Event Center in Costa Mesa, CA";
    static final String TEST_DATE = "Friday, March 17, 2017 at 12:00PM";
    static final String TEST_SECOND_DATE = "Friday, March 18, 2017 at 12:00PM";
    static final String TEST_LOCATION = "Costa Mesa, CA";
    static final String TEST_TICKET_URL = "http://www.bandsintown.com/event/12857771/buy_tickets?app_id=YOUR_APP_ID&artist=NOFX&came_from=67";
    static final String TEST_TICKET_TYPE = "Tickets";
    static final String TEST_TICKET_STATUS = "available";
    static final String TEST_DESCRIPTION = "Pot of Gold 2017 - VIP Experience Weekend Passes";
    static final String TEST_VENUE_NAME = "Rawhide Event Center";
    static final String TEST_VENUE_PLACE = "Rawhide Event Center";
    static final String TEST_VENUE_CITY = "Chandler";
    static final String TEST_VENUE_REGION = "AZ";
    static final String TEST_VENUE_COUNTRY = "United States";
    static final String TEST_VENUE_LONGITUDE = "33.2709964";
    static final String TEST_VENUE_LATITUDE = "-111.986939";
    private static final String TEST_DATE_TIME = "2017-04-08T21:00:00";


    //compares values in a Cursor against expected values
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
    }

    //compares a specific record in a Cursor against expected values
    private static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createArtistValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME, TEST_ARTIST);
        testValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE, TEST_ARTIST_IMAGE);
        testValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE, TEST_ARTIST_WEBSITE);
        testValues.put(ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP, TEST_TIME_STAMP);
        return testValues;
    }

    public static ContentValues createConcertValues(long artistRowId) {
        ContentValues concertValues = new ContentValues();
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TITLE, TEST_TITLE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DATE_TIME, TEST_DATE_TIME);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME, TEST_DATE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION, TEST_LOCATION);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_URL, TEST_TICKET_URL);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE, TEST_TICKET_TYPE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS, TEST_TICKET_STATUS);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION, TEST_DESCRIPTION);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME, TEST_VENUE_NAME);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE, TEST_VENUE_PLACE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY, TEST_VENUE_CITY);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_REGION, TEST_VENUE_REGION);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY, TEST_VENUE_COUNTRY);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE, TEST_VENUE_LONGITUDE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE, TEST_VENUE_LATITUDE);
        return concertValues;
    }

    public static ContentValues createDiffConcertValues(long artistRowId) {
        ContentValues concertValues = new ContentValues();
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TITLE, TEST_TITLE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DATE_TIME, TEST_DATE_TIME);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME, TEST_SECOND_DATE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION, TEST_LOCATION);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_URL, TEST_TICKET_URL);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE, TEST_TICKET_TYPE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS, TEST_TICKET_STATUS);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION, TEST_DESCRIPTION);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME, TEST_VENUE_NAME);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE, TEST_VENUE_PLACE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY, TEST_VENUE_CITY);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_REGION, TEST_VENUE_REGION);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY, TEST_VENUE_COUNTRY);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE, TEST_VENUE_LONGITUDE);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE, TEST_VENUE_LATITUDE);
        return concertValues;
    }


    //TestConcertsProvider uses this utility class to test the ContentObserver callbacks using
    //the PollingCheck class was taken from the Android CTS tests.
    //read more about Android CTS here: https://source.android.com/compatibility/cts/
    //its a good place to learn more about how to test Android Apps
    //PollingCheck works because the JUnit testing framework is not running on the main Android application thread.
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {

            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
