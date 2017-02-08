package com.drkhannah.concerts.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dhannah on 1/23/17.
 */

public class ConcertsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ConcertsContract() {}

    //A Content Authority is a symbolic name for the entire Content Provider
    //it needs to be unique, so we use the apps package name to create it
    public static final String CONTENT_AUTHORITY = "com.drkhannah.concerts.provider";

    //Use the Content Authority to create the Base URI that all apps will use to
    //communicate with the Content Provider
    //example Base Uri content://com.drkhannah.concerts.provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //paths that can be appended for to the BASE_URI to access data from the artist table and concert table
    //example path to concert table content://com.drkhannah.concerts.provider/concert
    public static final String PATH_ARTIST = "artist";
    public static final String PATH_CONCERT = "concert";

    // Inner class that defines the artist table contents
    // BaseColumns Interface provides a Primary Key _ID
    public static class ArtistEntry implements BaseColumns {

        public static final String TABLE_NAME = "artist";
        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ARTIST_IMAGE = "artist_image";
        public static final String COLUMN_ARTIST_WEBSITE = "artist_website";
        public static final String COLUMN_TIME_STAMP = "time_stamp";

        //URI that uses BASE_CONTENT_URI and appends "artist" to the path
        //example URI path to artist table content://com.drkhannah.concerts.provider/artist
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();

        //MIME type for returning multiple rows from the artist table
        //example vnd.android.cursor.dir/com.drkhannah.concerts.provider/artist
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        //MIME type for returning a single row from the artist table
        //example vnd.android.cursor.item/com.drkhannah.concerts.provider/artist
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        //builds a URI for the artist table and appends an _id
        //example URI content://com.drkhannah.concerts.provider/artist/_ID_HERE
        public static Uri buildArtistWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Inner class that defines the concert table contents
    // BaseColumns Interface provides a Primary Key _ID
    public static class ConcertEntry implements BaseColumns {
        public static final String TABLE_NAME = "concert";
        public static final String COLUMN_ARTIST_KEY = "artist_id";
        public static final String COLUMN_TTILE = "title";
        public static final String COLUMN_FORMATTED_DATE_TIME = "formated_date_time";
        public static final String COLUMN_FORMATTED_LOCATION = "formated_location";
        public static final String COLUMN_TICKET_URL = "ticket_url";
        public static final String COLUMN_TICKET_TYPE = "ticket_type";
        public static final String COLUMN_TICKET_STATUS = "ticket_status";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VENUE_NAME = "venue_name";
        public static final String COLUMN_VENUE_PLACE = "venue_place";
        public static final String COLUMN_VENUE_CITY = "venue_city";
        public static final String COLUMN_VENUE_REGION = "venue_region";
        public static final String COLUMN_VENUE_COUNTRY = "venue_country";
        public static final String COLUMN_VENUE_LONGITUDE = "venue_longitude";
        public static final String COLUMN_VENUE_LATITUDE = "venue_latitude";

        //URI that uses BASE_CONTENT_URI and appends "concert" to the path
        //example URI path to concert table content://com.drkhannah.concerts.provider/concert
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONCERT).build();

        //MIME type for returning multiple rows from the concert table
        //example vnd.android.cursor.dir/com.drkhannah.concerts.provider/concert
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONCERT;

        //MIME type for returning a single row from the concert table
        //example vnd.android.cursor.item/com.drkhannah.concerts.provider/concert
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONCERT;

        //builds a URI to get a list of concerts for a given artist
        //example URI path content://com.drkhannah.concerts.provider/concert/ARTIST_NAME_HERE
        public static Uri buildConcertListForArtistUri(String artist) {
            return CONTENT_URI.buildUpon().appendPath(artist).build();
        }

        //get the artist name from the the URI path created in the buildConcertListForArtistUri() method
        public static String getArtistNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        //builds a URI to get a single concert for an artist based on a given date
        //example URI path content://com.drkhannah.concerts.provider/concert/ARTIST_NAME_HERE/DATE_OF_CONCERT_HERE
        public static Uri buildConcertForArtistWithDate(String artist, String date) {
            return CONTENT_URI.buildUpon().appendPath(artist).appendPath(date).build();
        }

        //get the concert date from URI path created in the buildConcertForArtistWithDate() method
        public static String getConcertDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
}
