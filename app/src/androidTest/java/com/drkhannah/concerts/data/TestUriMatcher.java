package com.drkhannah.concerts.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Created by dhannah on 1/31/17.
 */

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


//This class utilizes constants that are declared with package protection inside of the ConcertsProvider, which is why
//the test must be in the same data package as the Android app code "com.drkhannah.concerts.data". It will test that the
//correct int value is returned for for the URIs that the ConcertsProvider can handle listed in the UriMatcher

@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {
    private static final String TEST_ARTIST_QUERY = "nofx";
    private static final String TEST_CONCERT_DATE = "Friday,%20March%2017,%202017%20at%2012:30PM";


    // content://com.drkhannah.concerts2/artist
    private static final Uri TEST_ARTIST_DIR = ConcertsContract.ArtistEntry.CONTENT_URI;

    // content://com.drkhannah.concerts2/concert
    private static final Uri TEST_CONCERTS_DIR = ConcertsContract.ConcertEntry.CONTENT_URI;

    // content://com.drkhannah.concerts2/concert/nofx
    private static final Uri TEST_CONCERT_LIST_FOR_ARTIST_DIR = ConcertsContract.ConcertEntry.buildConcertListForArtistUri(TEST_ARTIST_QUERY);

    // content://com.drkhannah.concerts2/concert/nofx/Friday,%20March%2017,%202017%20at%2012:30PM
    private static final Uri TEST_CONCERT_DATE_ITEM = ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(TEST_ARTIST_QUERY, TEST_CONCERT_DATE);


    @Test
    public void testUriMatcher() {
        UriMatcher testMatcher = ConcertsProvider.buildUriMatcher();

        assertEquals("Error: The ARTIST Uri was matched incorrectly.", testMatcher.match(TEST_ARTIST_DIR), ConcertsProvider.ARTIST);
        assertEquals("Error: The CONCERT Uri was matched incorrectly.", testMatcher.match(TEST_CONCERTS_DIR), ConcertsProvider.CONCERT);
        assertEquals("Error: The CONCERT_LIST_FOR_ARTIST Uri was matched incorrectly.", testMatcher.match(TEST_CONCERT_LIST_FOR_ARTIST_DIR), ConcertsProvider.CONCERT_LIST_FOR_ARTIST);
        assertEquals("Error: The CONCERT_DATE_ITEM Uri was matched incorrectly.", testMatcher.match(TEST_CONCERT_DATE_ITEM), ConcertsProvider.CONCERT_FOR_DATE);
    }

}
