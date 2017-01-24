package com.drkhannah.concerts.data;

import android.provider.BaseColumns;

/**
 * Created by dhannah on 1/23/17.
 */

public class ConcertsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ConcertsContract() {}

    // Inner class that defines the artist table contents
    // BaseColumns Interface provides a Primary Key _ID
    public static class ArtistEntry implements BaseColumns {
        public static final String TABLE_NAME = "artist";
        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ARTIST_IMAGE = "artist_image";
        public static final String COLUMN_ARTIST_WEBSITE = "artist_website";
        public static final String COLUMN_TIME_STAMP = "time_stamp";
    }

    // Inner class that defines the concert table contents
    // BaseColumns Interface provides a Primary Key _ID
    public static class ConcertEntry implements BaseColumns {
        public static final String TABLE_NAME = "concert";
        public static final String COLUMN_ARTIST_KEY = "artist_id";
        public static final String COLUMN_TTILE = "title";
        public static final String COLUMN_FORMATED_DATE_TIME = "formated_date_time";
        public static final String COLUMN_FORMATED_LOCATION = "formated_location";
        public static final String COLUMN_TICKET_URL = "ticket_url";
        public static final String COLUMN_TICKET_TYPE = "ticket_type";
        public static final String COLUMN_TICKET_STATUS = "ticket_status";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VENUE_NAME = "venue_name";
        public static final String COLUMN_VENUE_PLACE = "venue_place";
        public static final String COLUMN_VENUE_CITY = "venue_city";
        public static final String COLUMN_VENUE_COUNTRY = "venue_country";
        public static final String COLUMN_VENUE_LONGITUDE = "venue_longitude";
        public static final String COLUMN_VENUE_LATITUDE = "venue_latitude";
    }

}
