package com.drkhannah.concerts.data;

/**
 * Created by dhannah on 2/1/17.
 */

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestConcertsContract {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_ID = "1";
    private static final String TEST_CONCERT_DATE = "Friday, March 17, 2017 at 12:30PM";
    private static final String TEST_ARTIST = "nofx";


    @Test
    public void testBuildArtistWithIdUri() {
        Uri uri = ConcertsContract.ArtistEntry.buildArtistWithIdUri(1);
        assertNotNull("Error: Null Uri returned: ", uri);
        assertEquals("Error: Artist _ID not properly appended to the end of the Uri", TEST_ID, uri.getLastPathSegment());
        assertEquals("Error: Uri doesn't match the expected result", uri.toString(), "content://com.drkhannah.concerts.provider/artist/1");
    }

    @Test
    public void testBuildConcertListForArtistUri() {
        Uri uri = ConcertsContract.ConcertEntry.buildConcertListForArtistUri(TEST_ARTIST);
        assertNotNull("Error: Null Uri returned: ", uri);
        assertEquals("Error: Artist name not properly appended to the end of the Uri", TEST_ARTIST, ConcertsContract.ConcertEntry.getArtistNameFromUri(uri));
        assertEquals("Error: Uri doesn't match the expected result", uri.toString(), "content://com.drkhannah.concerts.provider/concert/" + TEST_ARTIST);

    }

    @Test
    public void testBuildConcertForArtistWithDate() {
        Uri uri = ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(TEST_ARTIST, TEST_CONCERT_DATE);
        assertNotNull("Error: Null Uri returned: ", uri);
        assertEquals("Error: Artist name not properly appended to the end of the Uri", TEST_ARTIST, ConcertsContract.ConcertEntry.getArtistNameFromUri(uri));
        assertEquals("Error: Concert Date not properly appended to the end of the Uri", TEST_CONCERT_DATE, ConcertsContract.ConcertEntry.getConcertDateFromUri(uri));
        assertEquals("Error: Uri doesn't match the expected result", uri.toString(), "content://com.drkhannah.concerts.provider/concert/nofx/Friday%2C%20March%2017%2C%202017%20at%2012%3A30PM");

    }

}
