package com.drkhannah.concerts.data;

import android.content.ContentValues;
import android.database.Cursor;

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

    //compares values in a Cursor against expected values
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    //compares a specific record in a Cursor against expected values
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
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

    static ContentValues createConcertValues(long artistRowId) {
        ContentValues concertValues = new ContentValues();
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TTILE, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATED_DATE_TIME, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATED_LOCATION, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_URL, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE, artistRowId);
        concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE, artistRowId);


        return concertValues;
    }
}
