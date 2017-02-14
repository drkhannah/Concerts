package com.drkhannah.concerts;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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

/**
 * Created by dhannah on 11/7/16.
 */

public class GetConcertsTask extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = GetConcertsTask.class.getSimpleName();

    private Context mContext;
    private final TextView mEmptyTextView;

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    public GetConcertsTask(Context context, TextView emptyTextView) {
        super();
        mContext = context;
        mEmptyTextView = emptyTextView;
    }

    //creates a new thread
    @Override
    protected String doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the artist.
        return downloadConcerts(params[0]);
    }

    @Override
    protected void onPostExecute(String responseString) {
        super.onPostExecute(responseString);
        if (responseString != null) {
            mEmptyTextView.setText(responseString);
        }
    }

    // Build a URL to request concerts for an artist
    private String downloadConcerts(String artistName) {
        // Will contain the raw JSON response as a string.
        String concertsJsonStr = null;

        // Declared outside try/catch so they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {

            //Build a uri to construct a valid url
            final String BASE_URL = mContext.getString(R.string.base_url);
            final String ARTIST = artistName;
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
                return mContext.getString(R.string.no_such_artist, Utils.getSharedPrefsArtistName(mContext));
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // there is nothing in the inputStream so return null
                return null;
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
                return mContext.getString(R.string.no_concerts_for, Utils.getSharedPrefsArtistName(mContext));
            }
            //convert the buffer to a string
            concertsJsonStr = buffer.toString();

            if (concertsJsonStr.equalsIgnoreCase("[]\n")) {
                // nothing in the concertsJsonStr so return null
                return mContext.getString(R.string.no_concerts_for, Utils.getSharedPrefsArtistName(mContext));
            }

            //return a parsed response List<Concert>
            Log.d(LOG_TAG, "RESPONSE FROM BANDSINTOWN: " + concertsJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
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
        return null;
    }

    private void parseJson(String concertsJsonStr) throws JSONException {
        //concertsJsonStr starts with a jsonArray
        final JSONArray concertsJsonArray = new JSONArray(concertsJsonStr);

        // Vector of ContentValues for concerts we will build and insert into the database
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(concertsJsonArray.length());

        //we will use the old artist _id to purge the artists old concert records
        long oldArtistId = 0;

        if (concertsJsonArray.length() > 0) {
            //get a concert object from the response
            JSONObject concertJsonObject = concertsJsonArray.getJSONObject(0);

            //artist array in response
            JSONArray artistsJsonArray = concertJsonObject.getJSONArray(mContext.getString(R.string.response_object_key_artists));
            JSONObject firstArtistJsonObject = artistsJsonArray.getJSONObject(0);
            String artistName = firstArtistJsonObject.optString(mContext.getString(R.string.response_object_key_name), mContext.getString(R.string.no_artist_name_available));
            String artistImage = firstArtistJsonObject.optString(mContext.getString(R.string.response_object_key_thumb_url), mContext.getString(R.string.no_artist_image_available));
            String artistWebsite = firstArtistJsonObject.optString(mContext.getString(R.string.response_object_key_website), mContext.getString(R.string.no_artist_website_available));

            oldArtistId = checkForArtist(artistName);

            long newArtistId = insertArtist(artistName, artistImage, artistWebsite);

            //loop through concertsJsonArray to get all concerts for the artist
            for (int i = 0; i < concertsJsonArray.length(); i++) {
                //get a concert object from the response
                concertJsonObject = concertsJsonArray.getJSONObject(i);

                //concert object in response
                String title = concertJsonObject.optString(mContext.getString(R.string.response_object_key_title), mContext.getString(R.string.no_title_available));
                String formattedDateTime = concertJsonObject.optString(mContext.getString(R.string.response_object_key_formatted_datetime), mContext.getString(R.string.no_date_available));
                String formattedLocation = concertJsonObject.optString(mContext.getString(R.string.response_object_key_formatted_location), mContext.getString(R.string.no_location_available));
                String ticketUrl = concertJsonObject.optString(mContext.getString(R.string.response_object_key_ticket_url), mContext.getString(R.string.no_ticket_url_available));
                String ticketType = concertJsonObject.optString(mContext.getString(R.string.response_object_key_ticket_type), mContext.getString(R.string.no_ticket_type_available));
                String ticketStatus = concertJsonObject.optString(mContext.getString(R.string.response_object_key_ticket_status), mContext.getString(R.string.no_ticket_status_available));
                String description = concertJsonObject.optString(mContext.getString(R.string.response_object_key_description), mContext.getString(R.string.no_description_available));

                //venue object in response
                JSONObject venue = concertJsonObject.getJSONObject(mContext.getString(R.string.response_object_key_venue));
                String venueName = venue.optString(mContext.getString(R.string.response_object_key_venue_name), mContext.getString(R.string.no_venue_name_available));
                String venuePlace = venue.optString(mContext.getString(R.string.response_object_key_place), mContext.getString(R.string.no_venue_place_available));
                String venueCity = venue.optString(mContext.getString(R.string.response_object_key_city), mContext.getString(R.string.no_venue_city_available));
                String venueRegion = venue.optString(mContext.getString(R.string.response_object_key_region), mContext.getString(R.string.no_venue_region_available));
                String venueCountry = venue.optString(mContext.getString(R.string.response_object_key_country), mContext.getString(R.string.no_venue_country_available));
                String venueLongitude = venue.optString(mContext.getString(R.string.response_object_key_longitude), mContext.getString(R.string.no_longitude_available));
                String venueLatitude = venue.optString(mContext.getString(R.string.response_object_key_latitude), mContext.getString(R.string.no_latitude_available));

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
                mContext.getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, concertsArray);
            }

            if (oldArtistId > 0) {
                purgeOldConcerts(oldArtistId);
            }

        }
    }

    public int purgeOldConcerts(long oldArtistId) {
        String oldId = String.valueOf(oldArtistId);

        //delete all concerts records with an artist_id
        //that matches the old artist _id
        return mContext.getContentResolver().delete(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY + " = ?", //selection
                new String[]{oldId} //selectionArgs
        );
    }

    public long checkForArtist(String artistName) {
        long artistId = 0;

        // check if the artist with this name exists in the db
        Cursor artistCursor = mContext.getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI, //URI
                new String[]{ConcertsContract.ArtistEntry._ID}, //projection
                ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " = ?", //selection
                new String[]{artistName}, //selectionArgs
                null //sortOrder
        );

        if (artistCursor.moveToFirst()) {
            //find the column index number for the artist _id column
            int artistIdIndex = artistCursor.getColumnIndex(ConcertsContract.ArtistEntry._ID);
            //get the artist _id
            artistId = artistCursor.getLong(artistIdIndex);
        }
        artistCursor.close();
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
        Uri insertedUri = mContext.getContentResolver().insert(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                artistValues
        );

        // The resulting URI contains the _id for the row.  Extract the artist _id from the Uri.
        artistId = ContentUris.parseId(insertedUri);

        return artistId;
    }
}

