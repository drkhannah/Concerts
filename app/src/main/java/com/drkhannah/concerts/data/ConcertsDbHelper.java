package com.drkhannah.concerts.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dhannah on 1/23/17.
 */

public class ConcertsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "concerts.db";

    public ConcertsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL Statement to CREATE artist table
        final String SQL_CREATE_ARTIST_TABLE =
                "CREATE TABLE " + ConcertsContract.ArtistEntry.TABLE_NAME + " (" +
                        ConcertsContract.ArtistEntry._ID + " INTEGER PRIMARY KEY," +
                        ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE," +
                        ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE + " TEXT," +
                        ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE + " TEXT," +
                        ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP + " TEXT NOT NULL)";

        //SQL Statement to CREATE concert table
        final String SQL_CREATE_CONCERT_TABLE =
                "CREATE TABLE " + ConcertsContract.ConcertEntry.TABLE_NAME + " (" +
                        ConcertsContract.ConcertEntry._ID + " INTEGER PRIMARY KEY," +
                        ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY + " INTEGER NOT NULL," +
                        ConcertsContract.ConcertEntry.COLUMN_TTILE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_TICKET_URL + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_REGION + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE + " TEXT," +

                        //ensures that duplicate concerts for an artist aren't added to the database
                        " UNIQUE (" + ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY + ", " +
                        ConcertsContract.ConcertEntry.COLUMN_TTILE + ", " +
                        ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME + ") ON CONFLICT REPLACE," +

                        // Set up the concerts_table COLUMN_ARTIST_KEY column as a foreign key to artist table.
                        " FOREIGN KEY (" + ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY + ") REFERENCES " +
                        ConcertsContract.ArtistEntry.TABLE_NAME + " (" + ConcertsContract.ArtistEntry._ID + "));";



        //execute the above SQL statments
        db.execSQL(SQL_CREATE_ARTIST_TABLE);
        db.execSQL(SQL_CREATE_CONCERT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //SQL Statement to DROP concert table
        final String SQL_DROP_CONCERT_TABLE = "DROP TABLE IF EXISTS " + ConcertsContract.ConcertEntry.TABLE_NAME;
        //SQL Statement to DROP artist table
        final String SQL_DROP_ARTIST_TABLE = "DROP TABLE IF EXISTS " + ConcertsContract.ArtistEntry.TABLE_NAME;

        //drop both artist and concert tables and recreate the database
        db.execSQL(SQL_DROP_CONCERT_TABLE);
        db.execSQL(SQL_DROP_ARTIST_TABLE);
        onCreate(db);
    }
}
