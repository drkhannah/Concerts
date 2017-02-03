package com.drkhannah.concerts;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.drkhannah.concerts.data.ConcertsContract;
import com.drkhannah.concerts.models.Concert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by dhannah on 11/7/16.
 */

public class GetConcertsTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = GetConcertsTask.class.getSimpleName();

    private List<Concert> mConcertList = new ArrayList<>();
    private Context mContext;

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    public GetConcertsTask(Context context) {
        super();
        mContext = context;
    }

    //creates a new thread
    @Override
    protected Void doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the artist.
        downloadConcerts(params[0]);
        return null;
    }

    // Build a URL to request concerts for an artist
    private void downloadConcerts(String artistToSearch) {
        // Will contain the raw JSON response as a string.
        String concertsJsonStr = null;

        // Declared outside try/catch so they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {

            //Build a uri to construct a valid url
            final String BASE_URL = mContext.getString(R.string.base_url);
            final String ARTIST = artistToSearch;
            final String RESPONSE_FORMAT = mContext.getString(R.string.response_format_param);
            final String API_VERSION = mContext.getString(R.string.api_version_param);
            final String APP_ID = mContext.getString(R.string.app_id_param);

            //build a valid URI
            Uri validUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(ARTIST)
                    .appendPath(RESPONSE_FORMAT)
                    .appendQueryParameter(API_VERSION, mContext.getString(R.string.api_version_value))
                    .appendQueryParameter(APP_ID, mContext.getString(R.string.app_id_value))
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
                return;
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // nothing in the StringBuffer so return null
                return;
            }
            //convert the buffer to a string
            concertsJsonStr = buffer.toString();

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

        // Vector of concert info we will build to insert into the database
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(concertsJsonArray.length());

        if (concertsJsonArray.length() > 0) {
            //loop through concertsJsonArray
            for (int i = 0; i < concertsJsonArray.length(); i++) {
                //get a concert object from the response
                JSONObject concertJsonObject = concertsJsonArray.getJSONObject(i);

                //concert object in response
                String title = concertJsonObject.optString(mContext.getString(R.string.response_object_key_title), mContext.getString(R.string.no_title_available));
                String formattedDateTime = concertJsonObject.optString(mContext.getString(R.string.response_object_key_formatted_datetime), mContext.getString(R.string.no_date_available));
                String formattedLocation = concertJsonObject.optString(mContext.getString(R.string.response_object_key_formatted_location), mContext.getString(R.string.no_location_available));
                String ticketUrl = concertJsonObject.optString(mContext.getString(R.string.response_object_key_ticket_url), mContext.getString(R.string.no_ticket_url_available));
                String ticketType = concertJsonObject.optString(mContext.getString(R.string.response_object_key_ticket_type), mContext.getString(R.string.no_ticket_type_available));
                String ticketStatus = concertJsonObject.optString(mContext.getString(R.string.response_object_key_ticket_status), mContext.getString(R.string.no_ticket_status_available));
                String description = concertJsonObject.optString(mContext.getString(R.string.response_object_key_description), mContext.getString(R.string.no_description_available));

                //artist array in response
                JSONArray artistsJsonArray = concertJsonObject.getJSONArray(mContext.getString(R.string.response_object_key_artists));
                JSONObject firstArtistJsonObject = artistsJsonArray.getJSONObject(0);
                String artistName = firstArtistJsonObject.optString(mContext.getString(R.string.response_object_key_name), mContext.getString(R.string.no_artist_name_available));
                String artistImage = firstArtistJsonObject.optString(mContext.getString(R.string.response_object_key_thumb_url), mContext.getString(R.string.no_artist_image_available));
                String artistWebsite = firstArtistJsonObject.optString(mContext.getString(R.string.response_object_key_website), mContext.getString(R.string.no_artist_website_available));

                //venue object in response
                JSONObject venue = concertJsonObject.getJSONObject(mContext.getString(R.string.response_object_key_venue));
                String venueName = venue.optString(mContext.getString(R.string.response_object_key_venue_name), mContext.getString(R.string.no_venue_name_available));
                String venuePlace = venue.optString(mContext.getString(R.string.response_object_key_place), mContext.getString(R.string.no_venue_place_available));
                String venueCity = venue.optString(mContext.getString(R.string.response_object_key_city), mContext.getString(R.string.no_venue_city_available));
                String venueRegion = venue.optString(mContext.getString(R.string.response_object_key_region), mContext.getString(R.string.no_venue_city_available));
                String venueCountry = venue.optString(mContext.getString(R.string.response_object_key_country), mContext.getString(R.string.no_venue_country_available));
                String venueLongitude = venue.optString(mContext.getString(R.string.response_object_key_longitude), mContext.getString(R.string.no_longitude_available));
                String venueLatitude = venue.optString(mContext.getString(R.string.response_object_key_latitude), mContext.getString(R.string.no_latitude_available));

                long artistId = addArtist(artistName, artistImage, artistWebsite);

                ContentValues concertValues = new ContentValues();

                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY, artistId);
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

                contentValuesVector.add(concertValues);
            }

            // add to database
            if (contentValuesVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, cvArray);
            }
        }
    }

    public long addArtist(String artistName, String img_url, String website_url) {
        long artistId;

        // First, check if the artist with this name exists in the db
        Cursor artistCursor = mContext.getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                new String[]{ConcertsContract.ArtistEntry._ID},
                ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " = ?",
                new String[]{artistName},
                null);

        //get the artist record ID
        if (artistCursor.moveToFirst()) {
            int artistIdIndex = artistCursor.getColumnIndex(ConcertsContract.ArtistEntry._ID);
            artistId = artistCursor.getLong(artistIdIndex);
        } else {
            //get a current Timestamp
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();

            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues artistValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            artistValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME, artistName);
            artistValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE, website_url);
            artistValues.put(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE, img_url);
            artistValues.put(ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP, timestamp);

            // Finally, insert artist record.
            Uri insertedUri = mContext.getContentResolver().insert(
                    ConcertsContract.ArtistEntry.CONTENT_URI,
                    artistValues
            );

            // The resulting URI contains the ID for the row.  Extract the artist ID from the Uri.
            artistId = ContentUris.parseId(insertedUri);
        }

        artistCursor.close();
        return artistId;
    }
}

