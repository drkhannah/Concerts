package com.drkhannah.concerts.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by dhannah on 1/31/17.
 */

public class ConcertsProvider extends ContentProvider {

    private ConcertsDbHelper mDbHelper;

    //UriMatcher used to define the URI's this ContentProvider can handle
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //codes for URIs in UriMatcher
    public static final int ARTIST = 1;
    public static final int CONCERT = 2;
    public static final int CONCERT_LIST_FOR_ARTIST = 3;
    public static final int CONCERT_FOR_DATE = 4;

    private static final SQLiteQueryBuilder sConcertsForArtistQueryBuilder;

    static {
        sConcertsForArtistQueryBuilder = new SQLiteQueryBuilder();
        //This is an SQL INNER JOIN
        //example: concert INNER JOIN artist ON concert.artist_id = artist._id
        sConcertsForArtistQueryBuilder.setTables(
                ConcertsContract.ConcertEntry.TABLE_NAME + " INNER JOIN " +
                        ConcertsContract.ArtistEntry.TABLE_NAME +
                        " ON " + ConcertsContract.ConcertEntry.TABLE_NAME +
                        "." + ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY +
                        " = " + ConcertsContract.ArtistEntry.TABLE_NAME +
                        "." + ConcertsContract.ArtistEntry._ID);
    }

    //selection for artist name
    //example: artist.artist_name = ?
    public static final String sArtistSelection =
            ConcertsContract.ArtistEntry.TABLE_NAME +
                    "." + ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " = ? ";

    //returns a Cursor of concerts for a given artist
    private Cursor getConcertListForArtist(Uri uri, String[] projection, String sortOrder) {
        String artistName = ConcertsContract.ConcertEntry.getArtistNameFromUri(uri);
        String selection = sArtistSelection;
        String[] selectionArgs = new String[]{artistName};

        return sConcertsForArtistQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    //selection for artist name and concert date
    //example: artist.artist_name = ? AND formatted_date_time = ?
    public static final String sArtistAndDateSelection =
            ConcertsContract.ArtistEntry.TABLE_NAME +
                    "." + ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " = ? AND " +
                    ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME + " = ? ";

    //returns a Cursor of a single concert for an artist on a given date
    private Cursor getConcertForArtistWithDate(Uri uri, String[] projection, String sortOrder) {
        String artistName = ConcertsContract.ConcertEntry.getArtistNameFromUri(uri);
        String concertDate = ConcertsContract.ConcertEntry.getConcertDateFromUri(uri);
        String selection = sArtistAndDateSelection;
        String[] selectionArgs = new String[]{artistName, concertDate};

        return sConcertsForArtistQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public static UriMatcher buildUriMatcher() {
        //each path added to the UriMatcher has a code that is returned when that URI is requested
        //The code passed to the UriMatcher's constructor represents the code to return
        //for the root URI. For the root URI we pass NO_MATCH.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ConcertsContract.CONTENT_AUTHORITY;

        //below are each URI we want to add to the UriMatcher
        //These will be the URI's that this Content Provider can handle
        //in these paths *'s match TEXT and #'s match only NUMBERS

        //example content://com.drkhannah.concerts.provider/artist
        uriMatcher.addURI(authority, ConcertsContract.PATH_ARTIST, ARTIST);
        //example content://com.drkhannah.concerts.provider/concert
        uriMatcher.addURI(authority, ConcertsContract.PATH_CONCERT, CONCERT);
        //example content://com.drkhannah.concerts.provider/concert/ARTIST_NAME_HERE
        uriMatcher.addURI(authority, ConcertsContract.PATH_CONCERT + "/*", CONCERT_LIST_FOR_ARTIST);
        //example content://com.drkhannah.concerts.provider/concert/ARTIST_NAME_HERE/DATE_OF_CONCERT_HERE
        uriMatcher.addURI(authority, ConcertsContract.PATH_CONCERT + "/*/*", CONCERT_FOR_DATE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        //create a new ConcertsDbHelper that we can use later
        mDbHelper = new ConcertsDbHelper(getContext());
        return false;
    }

    //handles requests for MIME types
    //returns the MIME type for each path in the UriMatcher
    @Nullable
    @Override
    public String getType(Uri uri) {

        //use the UriMatcher to so we can write a switch based on the URI that was passed in
        final int match = sUriMatcher.match(uri);

        //"match" will be one of the codes we assigned to each URI in the UriMatcher
        switch (match) {
            case ARTIST:
                //return MULTIPLE row MIME type
                return ConcertsContract.ArtistEntry.CONTENT_TYPE;
            case CONCERT:
                //return MULTIPLE row MIME type
                return ConcertsContract.ConcertEntry.CONTENT_TYPE;
            case CONCERT_LIST_FOR_ARTIST:
                //return MULTIPLE row MIME type
                return ConcertsContract.ConcertEntry.CONTENT_TYPE;
            case CONCERT_FOR_DATE:
                //return SINGLE row MIME type
                return ConcertsContract.ConcertEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unhandled uri: " + uri);
        }
    }

    //Below are all the Content Providers CRUD methods
    //query method for this Content Provider
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Cursor to be returned from this method
        Cursor returnCursor;

        //use the UriMatcher to so we can write a switch based on the URI that was passed in
        final int match = sUriMatcher.match(uri);

        //"match" will be one of the codes we assigned to each URI in the UriMatcher
        switch (match) {
            // "artist"
            case ARTIST: {
                returnCursor = mDbHelper.getReadableDatabase().query(
                        ConcertsContract.ArtistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "concert"
            case CONCERT: {
                returnCursor = mDbHelper.getReadableDatabase().query(
                        ConcertsContract.ConcertEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            //"concert/*"
            case CONCERT_LIST_FOR_ARTIST:
                returnCursor = getConcertListForArtist(uri, projection, sortOrder);
                break;
            //"concert/*/*
            case CONCERT_FOR_DATE:
                returnCursor = getConcertForArtistWithDate(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unhandled uri: " + uri);
        }
        //watch for changes on a URI and any of its decedents
        //this allows the content provider to alert the UI when a cursor changes
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    //insert method for this Content Provider
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //get a writable database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //use the UriMatcher to so we can write a switch based on the URI that was passed in
        final int match = sUriMatcher.match(uri);

        //URI to return
        Uri returnUri;

        //"match" will be one of the codes we assigned to each URI in the UriMatcher
        //this handles inserting SINGLE artist and record and SINGLE concert record
        //inserting MULTIPLE concert records will be handled with the bulkInsert() method
        switch (match) {
            //"artist"
            case ARTIST: {
                long _id = db.insert(ConcertsContract.ArtistEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ConcertsContract.ArtistEntry.buildArtistWithIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unhandled uri: " + uri);
        }
        //watch for changes on a URI and any of its decedents
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //get a writable database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //use the UriMatcher to so we can write a switch based on the URI that was passed in
        final int match = sUriMatcher.match(uri);

        //rows deleted to return
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( selection == null ) selection = "1";
        switch (match) {
            //"artist"
            case ARTIST:
                rowsDeleted = db.delete(ConcertsContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            //"concert"
            case CONCERT:
                rowsDeleted = db.delete(ConcertsContract.ConcertEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //get a writable database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //use the UriMatcher to so we can write a switch based on the URI that was passed in
        final int match = sUriMatcher.match(uri);

        //rows updated to return
        int rowsUpdated;

        switch (match) {
            //"artist"
            case ARTIST:
                rowsUpdated = db.update(ConcertsContract.ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            //"concert"
            case CONCERT:
                rowsUpdated = db.update(ConcertsContract.ConcertEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    //handles inserting MULTIPLE concerts at once
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        //get a writable database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //use the UriMatcher to so we can write a switch based on the URI that was passed in
        final int match = sUriMatcher.match(uri);

        switch (match) {
            //"concert"
            case CONCERT:
                db.beginTransaction();
                int returnCount = 0;
                try{
                    //loop through the array of ContentValues and insert each record
                    for (ContentValues value : values) {
                        long _id = db.insert(ConcertsContract.ConcertEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // This is a method only here to help our unit tests
    // It shuts down the Content Provider so that test methods aren't creating multiple instances of it
    // read more about it here: http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
