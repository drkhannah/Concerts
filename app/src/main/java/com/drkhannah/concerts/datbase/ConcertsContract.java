package com.drkhannah.concerts.datbase;

import android.provider.BaseColumns;

/**
 * Created by dhannah on 1/23/17.
 */

public class ConcertsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ConcertsContract() {}

    // Inner class that defines the artist_table contents
    // BaseColumns Interface provides a Primary Key _ID
    public static class ArtistEntry implements BaseColumns {
        public static final String TABLE_NAME = "artist_table";
        public static final String COLUMN_NAME_ARTIST_NAME = "artist_name";
        public static final String COLUMN_NAME_ARTIST_IMAGE = "artist_image";
        public static final String COLUMN_NAME_ARTIST_WEBSITE = "artist_website";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";
    }

    // Inner class that defines the concert_table contents
    // BaseColumns Interface provides a Primary Key _ID
    public static class ConcertEntry implements BaseColumns {
        public static final String TABLE_NAME = "concert_table";
        public static final String COLUMN_NAME_ARTIST_ID = "artist_id";
        public static final String COLUMN_NAME_TTILE = "title";
        public static final String COLUMN_NAME_FORMATED_DATE_TIME = "formated_date_time";
        public static final String COLUMN_NAME_FORMATED_LOCATION = "formated_location";
        public static final String COLUMN_NAME_TICKET_URL = "ticket_url";
        public static final String COLUMN_NAME_TICKET_TYPE = "ticket_type";
        public static final String COLUMN_NAME_TICKET_STATUS = "ticket_status";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_VENUE_NAME = "venue_name";
        public static final String COLUMN_NAME_VENUE_PLACE = "venue_place";
        public static final String COLUMN_NAME_VENUE_CITY = "venue_city";
        public static final String COLUMN_NAME_VENUE_COUNTRY = "venue_country";
        public static final String COLUMN_NAME_VENUE_LONGITUDE = "venue_longitude";
        public static final String COLUMN_NAME_VENUE_LATITUDE = "venue_latitude";
    }

}
