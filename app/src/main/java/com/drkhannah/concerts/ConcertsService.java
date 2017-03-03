package com.drkhannah.concerts;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.drkhannah.concerts.data.ConcertsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.drkhannah.concerts.R.string.extra_artist_name;

/**
 * Created by dhannah on 2/23/17.
 */

public class ConcertsService extends IntentService {

    private static final String LOG_TAG = ConcertsService.class.getSimpleName();

    public ConcertsService() {
        super("ConcertsService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        //get the artist name from the Intent
        String artistName = intent.getStringExtra(getString(extra_artist_name));

        // Will contain the raw JSON response as a string.
        String concertsJsonStr = null;

        // Declared outside try/catch so they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            //Build a uri to construct a valid url
            final String BASE_URL = getString(R.string.base_url);
            final String RESPONSE_FORMAT = getString(R.string.response_format_param);
            final String API_VERSION = getString(R.string.api_version_param);
            final String APP_ID = getString(R.string.app_id_param);

            //build a valid URI
            Uri validUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(artistName)
                    .appendPath(RESPONSE_FORMAT)
                    .appendQueryParameter(API_VERSION, getString(R.string.api_version_value))
                    .appendQueryParameter(APP_ID, getString(R.string.app_id_value))
                    .build();

            //create a URL from the URI we built above
            URL url = new URL(validUri.toString());
            Log.d(LOG_TAG, "URL for request: " + url);

            // Create a GET request to the BandsInTown api and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(LOG_TAG, "Request Response Code: " + urlConnection.getResponseCode());
                //send local broadcast to ConcertListFragment
                sendEmptyTextViewLocalBroadcast(getString(R.string.no_such_artist, Utils.getSharedPrefsArtistName(getApplicationContext())));
                sendJobFinishedBroadcast();
                return;
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();

            if (inputStream == null) {
                // there is nothing in the inputStream so return null
                return;
            }

            //read the input stream
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Read the inputStream line by line into the StringBuffer
                // adding a line break after each line
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() == 0) {
                // nothing in the StringBuffer so return null
                //send local broadcast to ConcertListFragment
                sendEmptyTextViewLocalBroadcast(getString(R.string.no_concerts_for, Utils.getSharedPrefsArtistName(getApplicationContext())));
                sendJobFinishedBroadcast();
                return;
            }

            //convert the stringBuilder to a string
            concertsJsonStr = stringBuilder.toString();

            if (concertsJsonStr.equalsIgnoreCase("[]\n")) {
                // nothing in the concertsJsonStr so return null
                //send local broadcast to ConcertListFragment
                sendEmptyTextViewLocalBroadcast(getString(R.string.no_concerts_for, Utils.getSharedPrefsArtistName(getApplicationContext())));
                sendJobFinishedBroadcast();
                return;
            }

            //return a parsed response List<Concert>
            Log.d(LOG_TAG, "RESPONSE FROM BANDSINTOWN: " + concertsJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        } finally {
            //no matter success or error of try/catch
            //close the urlConnection and the reader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            parseJson(concertsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseJson(String concertsJsonStr) throws JSONException {
        //concertsJsonStr starts with a jsonArray
        final JSONArray concertsJsonArray = new JSONArray(concertsJsonStr);

        // Vector of ContentValues for concerts we will build and insert into the database
        Vector<ContentValues> contentValuesVector = new Vector<>(concertsJsonArray.length());

        //we will use the old artist _id to purge the artists old concert records
        long oldArtistId;

        if (concertsJsonArray.length() > 0) {
            //get a concert object from the response
            JSONObject concertJsonObject = concertsJsonArray.getJSONObject(0);

            //artist array in response
            JSONArray artistsJsonArray = concertJsonObject.getJSONArray(getString(R.string.response_object_key_artists));
            JSONObject firstArtistJsonObject = artistsJsonArray.getJSONObject(0);
            String artistName = firstArtistJsonObject.optString(getString(R.string.response_object_key_name), getString(R.string.no_artist_name_available));
            String artistImage = firstArtistJsonObject.optString(getString(R.string.response_object_key_thumb_url), getString(R.string.no_artist_image_available));
            String artistWebsite = firstArtistJsonObject.optString(getString(R.string.response_object_key_website), getString(R.string.no_artist_website_available));

            oldArtistId = checkForArtist(artistName);

            long newArtistId = insertArtist(artistName, artistImage, artistWebsite);

            //loop through concertsJsonArray to get all concerts for the artist
            for (int i = 0; i < concertsJsonArray.length(); i++) {
                //get a concert object from the response
                concertJsonObject = concertsJsonArray.getJSONObject(i);

                //concert object in response
                String title = concertJsonObject.optString(getString(R.string.response_object_key_title), getString(R.string.no_title_available));
                String formattedDateTime = concertJsonObject.optString(getString(R.string.response_object_key_formatted_datetime), getString(R.string.no_date_available));
                String formattedLocation = concertJsonObject.optString(getString(R.string.response_object_key_formatted_location), getString(R.string.no_location_available));
                String ticketUrl = concertJsonObject.optString(getString(R.string.response_object_key_ticket_url), getString(R.string.no_ticket_url_available));
                String ticketType = concertJsonObject.optString(getString(R.string.response_object_key_ticket_type), getString(R.string.no_ticket_type_available));
                String ticketStatus = concertJsonObject.optString(getString(R.string.response_object_key_ticket_status), getString(R.string.no_ticket_status_available));
                String description = concertJsonObject.optString(getString(R.string.response_object_key_description), getString(R.string.no_description_available));

                //venue object in response
                JSONObject venue = concertJsonObject.getJSONObject(getString(R.string.response_object_key_venue));
                String venueName = venue.optString(getString(R.string.response_object_key_venue_name), getString(R.string.no_venue_name_available));
                String venuePlace = venue.optString(getString(R.string.response_object_key_place), getString(R.string.no_venue_place_available));
                String venueCity = venue.optString(getString(R.string.response_object_key_city), getString(R.string.no_venue_city_available));
                String venueRegion = venue.optString(getString(R.string.response_object_key_region), getString(R.string.no_venue_region_available));
                String venueCountry = venue.optString(getString(R.string.response_object_key_country), getString(R.string.no_venue_country_available));
                String venueLongitude = venue.optString(getString(R.string.response_object_key_longitude), getString(R.string.no_longitude_available));
                String venueLatitude = venue.optString(getString(R.string.response_object_key_latitude), getString(R.string.no_latitude_available));

                ContentValues concertValues = new ContentValues();

                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY, newArtistId);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TTILE, title);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME, formattedDateTime);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION, formattedLocation);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_URL, ticketUrl);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE, ticketType);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS, ticketStatus);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION, description);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME, venueName);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE, venuePlace);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY, venueCity);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_REGION, venueRegion);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY, venueCountry);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE, venueLongitude);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE, venueLatitude);

                //add this concert's values to the Vector<ContentValues>
                contentValuesVector.add(concertValues);
            }

            if (contentValuesVector.size() > 0) {
                //convert Vector<ContentValues> into an Array
                ContentValues[] concertsArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(concertsArray);

                // bulkInsert concerts into database
                getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, concertsArray);
            }

            if (oldArtistId > 0) {
                purgeOldConcerts(oldArtistId);
            }

            sendJobFinishedBroadcast();
        }
    }

    public int purgeOldConcerts(long oldArtistId) {
        String oldId = String.valueOf(oldArtistId);

        //delete all concerts records with an artist_id
        //that matches the old artist _id
        return getContentResolver().delete(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY + " = ?", //selection
                new String[]{oldId} //selectionArgs
        );
    }

    public long checkForArtist(String artistName) {
        long artistId = 0;

        // check if the artist with this name exists in the db
        Cursor artistCursor = getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI, //URI
                new String[]{ConcertsContract.ArtistEntry._ID}, //projection
                ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " = ?", //selection
                new String[]{artistName}, //selectionArgs
                null //sortOrder
        );

        if (artistCursor != null && artistCursor.moveToFirst()) {
            //find the column index number for the artist _id column
            int artistIdIndex = artistCursor.getColumnIndex(ConcertsContract.ArtistEntry._ID);
            //get the artist _id
            artistId = artistCursor.getLong(artistIdIndex);
        }
        if (artistCursor != null) {
            artistCursor.close();
        }
        return artistId;
    }

    public long insertArtist(String artistName, String img_url, String website_url) {
        long artistId;

        //get a current time
        long timestamp = System.currentTimeMillis();

        // create a ContentValues object to hold the data you want to insert for the artist record.
        ContentValues artistValues = new ContentValues();

        // add the artist values
        artistValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME, artistName.toLowerCase());
        artistValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE, img_url);
        artistValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE, website_url);
        artistValues.put(ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP, timestamp);

        // Finally, insert artist record.
        Uri insertedUri = getContentResolver().insert(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                artistValues
        );

        // The resulting URI contains the _id for the row.  Extract the artist _id from the Uri.
        artistId = ContentUris.parseId(insertedUri);

        return artistId;
    }

    //sends local broadcast to update empty textview in ConcertListFragment
    private void sendEmptyTextViewLocalBroadcast(String string) {
        Intent emptyTextViewIntent = new Intent(getString(R.string.empty_text_action));
        emptyTextViewIntent.putExtra(getString(R.string.empty_text_view_extra), string);
        LocalBroadcastManager.getInstance(this).sendBroadcast(emptyTextViewIntent);
    }

    //send job finished broadcast to ConcertsJobService so it can call jobFinished()
    private void sendJobFinishedBroadcast() {
        Intent jobFinishedIntent = new Intent(getString(R.string.job_finished_action));
        LocalBroadcastManager.getInstance(this).sendBroadcast(jobFinishedIntent);
    }
}
