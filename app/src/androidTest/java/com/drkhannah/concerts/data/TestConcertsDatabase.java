package com.drkhannah.concerts.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestConcertsDatabase {

    Context mContext = InstrumentationRegistry.getTargetContext();

    //setUP() method gets called when this test class is run.
    //We delete the database to start fresh
    @Before
    public void deleteDatabase() {
        mContext.deleteDatabase(ConcertsDbHelper.DATABASE_NAME);
    }

    @Test
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNamesHashSet = new HashSet<String>();
        tableNamesHashSet.add(ConcertsContract.ArtistEntry.TABLE_NAME);
        tableNamesHashSet.add(ConcertsContract.ConcertEntry.TABLE_NAME);

        // Get reference to writable database
        SQLiteDatabase db = new ConcertsDbHelper(mContext).getWritableDatabase();
        //assert that the database was opened
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        // every SQLite database has an SQLITE_MASTER table that defines the schema for the database.
        // We can use it to find the table names
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        //see if we got back at least one table name returned from the query by using
        //the cursor's moveToFirst() method
        assertTrue("Error: No table names returned from sqlite_master", cursor.moveToFirst());

        // verify that the tables have been created
        // by looping through the cursor
        // and removing items from the HashSet simultaneously
        do {
            tableNamesHashSet.remove(cursor.getString(0));
        } while(cursor.moveToNext());

        // if this fails, it means that the database doesn't contain both the artist table and concert table
        assertTrue("Error: Your database was created without both the artist table and concert table", tableNamesHashSet.isEmpty());

        // now, test the column names in the tables, PRAGMA statements in SQLite return info on tables and indexes
        // you an use a PRAGMA statement to get info about the schema of a certain table
        cursor = db.rawQuery("PRAGMA table_info(" + ConcertsContract.ArtistEntry.TABLE_NAME + ")", null);

        // assert that we got information back for the artist table
        assertTrue("Error: unable to query the database for artist table information.", cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for in the artist table
        final HashSet<String> artistTableColumnHashSet = new HashSet<String>();
        artistTableColumnHashSet.add(ConcertsContract.ArtistEntry._ID);
        artistTableColumnHashSet.add(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME);
        artistTableColumnHashSet.add(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE);
        artistTableColumnHashSet.add(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE);
        artistTableColumnHashSet.add(ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP);

        // verify that the correct column names have been created in the artist table
        // by looping through the cursor
        // and removing items from the HashSet simultaneously
        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            artistTableColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required artist entry columns
        assertTrue("Error: The database doesn't contain all of the required artist entry columns", artistTableColumnHashSet.isEmpty());

        // now, test the column names in the tables, PRAGMA statements in SQLite return info on tables and indexes
        // you an use a PRAGMA statement to get info about the schema of a certain table
        cursor = db.rawQuery("PRAGMA table_info(" + ConcertsContract.ConcertEntry.TABLE_NAME + ")", null);

        // assert that we got information back for the artist table
        assertTrue("Error: unable to query the database for concert table information.", cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for in the artist table
        final HashSet<String> concertTableColumnHashSet = new HashSet<String>();
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry._ID);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_TTILE);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_FORMATED_DATE_TIME);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_FORMATED_LOCATION);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_TICKET_URL);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE);
        concertTableColumnHashSet.add(ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE);

        // verify that the correct column names have been created in the concert table
        // by looping through the cursor
        // and removing items from the HashSet simultaneously
        columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            concertTableColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required concert entry columns
        assertTrue("Error: The database doesn't contain all of the required artist entry columns", concertTableColumnHashSet.isEmpty());

        //close the Cursor and Database
        cursor.close();
        db.close();
    }

    @Test
    public void testArtistTable() {
        insertArtist();
    }

    @Test
    public void testConcertTable() {
        // Insert an artist, and then use the locationRowId to insert a concert.

        long artistRowId = insertArtist();

        // Make sure we got back a row ID.
        assertFalse("Error: Artist Not Inserted Correctly", artistRowId == -1L);

        // Get reference to writable database
        SQLiteDatabase db = new ConcertsDbHelper(mContext).getWritableDatabase();

        // Create concert values
        ContentValues weatherValues = TestUtils.createWeatherValues(artistRowId);
    }

    public long insertArtist() {
        // Get reference to writable database
        SQLiteDatabase db = new ConcertsDbHelper(mContext).getWritableDatabase();

        // a ContentValues object is map of key-value pairs that can be inserted into a database
        // This ContentValues object is created by a method in the TestUtils class
        ContentValues testValues = TestUtils.createArtistValues();

        // Insert ContentValues into database and get a row ID back
        long artistRowId = db.insert(ConcertsContract.ArtistEntry.TABLE_NAME, null, testValues);

        // Verify we got a row ID back.
        assertTrue(artistRowId != -1);

        // Data was inserted, now lets pull it out and look at it
        Cursor cursor = db.query(
                ConcertsContract.ArtistEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // check to see if we got any records back from the query
        assertTrue( "Error: No Records returned from artist query", cursor.moveToFirst() );

        // compare data in Cursor with the original ContentValues
        // validateCurrentRecord method is in TestUtils
        TestUtils.validateCurrentRecord("Error: artist query validation failed", cursor, testValues);

        // try to move Cursor to next record to prove that there is only one record in the database
        assertFalse( "Error: More than one record returned from artist query", cursor.moveToNext() );

        // Close Cursor and Database
        cursor.close();
        db.close();
        return artistRowId;
    }

}
