package com.drkhannah.concerts.datbase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dhannah on 1/23/17.
 */

public class ConcertsDatabase extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Concerts.db";

    public ConcertsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL Statement to CREATE artist_table
        final String SQL_CREATE_ARTIST_TABLE =
                "CREATE TABLE " + ConcertsContract.ArtistEntry.TABLE_NAME + " (" +
                        ConcertsContract.ArtistEntry._ID + " INTEGER PRIMARY KEY," +
                        ConcertsContract.ArtistEntry.COLUMN_NAME_ARTIST_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE," +
                        ConcertsContract.ArtistEntry.COLUMN_NAME_ARTIST_IMAGE + " TEXT," +
                        ConcertsContract.ArtistEntry.COLUMN_NAME_ARTIST_WEBSITE + " TEXT," +
                        ConcertsContract.ArtistEntry.COLUMN_NAME_TIME_STAMP + " TEXT NOT NULL)";

        //SQL Statement to DROP artist_table
        final String SQL_DROP_ARTIST_TABLE =
                "DROP TABLE IF EXISTS " + ConcertsContract.ArtistEntry.TABLE_NAME;

        //SQL Statement to CREATE concert_table
        final String SQL_CREATE_CONCERT_TABLE =
                "CREATE TABLE " + ConcertsContract.ConcertEntry.TABLE_NAME + " (" +
                        ConcertsContract.ConcertEntry._ID + " INTEGER PRIMARY KEY," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_ARTIST_ID + " TEXT NOT NULL," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_TTILE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_FORMATED_DATE_TIME + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_FORMATED_LOCATION + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_TICKET_URL + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_TICKET_TYPE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_TICKET_STATUS + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_VENUE_NAME + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_VENUE_PLACE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_VENUE_CITY + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_VENUE_COUNTRY + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_VENUE_LONGITUDE + " TEXT," +
                        ConcertsContract.ConcertEntry.COLUMN_NAME_VENUE_LATITUDE + " TEXT)" +

                        // Set up the concerts_table COLUMN_NAME_ARTIST_ID column as a foreign key to artist_table.
                        "FOREIGN KEY (" + ConcertsContract.ConcertEntry.COLUMN_NAME_ARTIST_ID + ") REFERENCES " +
                        ConcertsContract.ArtistEntry.TABLE_NAME + " (" + ConcertsContract.ArtistEntry._ID + "));";

        //SQL Statement to DROP concert_table
        final String SQL_DROP_CONCERT_TABLE =
                "DROP TABLE IF EXISTS " + ConcertsContract.ConcertEntry.TABLE_NAME;

        //execute the above SQL statments
        db.execSQL(SQL_CREATE_ARTIST_TABLE);
        db.execSQL(SQL_CREATE_CONCERT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop both tables and recreate the database
        db.execSQL("DROP TABLE IF EXISTS " + ConcertsContract.ArtistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ConcertsContract.ConcertEntry.TABLE_NAME);
        onCreate(db);
    }
}
