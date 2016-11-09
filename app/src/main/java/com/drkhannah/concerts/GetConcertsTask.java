package com.drkhannah.concerts;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhannah on 11/7/16.
 */

public class GetConcertsTask extends AsyncTask<String, Void, List<Concert>> {

    private static final String LOG_TAG = GetConcertsTask.class.getSimpleName();

    private TextView mMainTextView;
    private Context mContext;

    private List<Concert> mConcertList = new ArrayList<>();

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     * This receives a TextView and sets its text in onPostExecute()
     */
    public GetConcertsTask(Context context, TextView mainTextView) {
        super();
        mMainTextView = mainTextView;
        mContext = context;
    }

    //creates a new thread
    @Override
    protected List<Concert> doInBackground(String... params) {

        // params comes from the execute() call: params[0] is the artist.
        try {
            return downloadConcerts(params[0]);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to URL", e);
            return null;
        }
    }

    // onPostExecute delivers the results of  doInBackground() on the UI thread.
    @Override
    protected void onPostExecute(List<Concert> result) {
        mMainTextView.setText(result.get(0).getTitle());
    }

    // Build a URL to request concerts for an artist
    private List<Concert> downloadConcerts(String artistToSearch) throws IOException {

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
                return null;
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
                return null;
            }

            //convert the buffer to a string
            String concertsJsonStr = buffer.toString();

            Log.d(LOG_TAG, "RESPONSE FROM BANDSINTOWN: " + concertsJsonStr);

            try {
                return parseJson(concertsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        return null;
    }

    private List<Concert> parseJson(String concertsJsonStr) throws JSONException {

        //Keys in concertsJsonStr
        final String CONCERT_TITLE = "title";
        final String CONCERT_FORMATTED_DATETIME = "formatted_datetime";
        final String CONCERT_FORMATTED_LOCATION = "formatted_location";
        final String CONCERT_TICKET_URL = "ticket_url";
        final String CONCERT_TICKET_TYPE = "ticket_type";
        final String CONCERT_TICKET_STATUS = "ticket_status";
        final String CONCERT_DESCRIPTION = "description";

        final String ARTISTS_ARRAY = "artists";
        final String ARTIST_NAME = "name";
        final String ARTIST_IMAGE = "thumb_url";
        final String ARTIST_WEBSITE = "website";

        final String VENUE_OBJECT = "venue";
        final String VENUE_NAME = "name";
        final String VENUE_PLACE = "place";
        final String VENUE_CITY = "city";
        final String VENUE_COUNTRY = "country";
        final String VENUE_LONGITUDE = "longitude";
        final String VENUE_LATITUDE = "latitude";

        //concertsJsonStr starts with a jsonArray
        final JSONArray concertsJsonArray = new JSONArray(concertsJsonStr);

        if (concertsJsonArray.length() > 0) {
            for (int i = 0; i < concertsJsonArray.length(); i++) {
                JSONObject concertJsonObject = concertsJsonArray.getJSONObject(i);

                //concert object
                String title = concertJsonObject.getString(CONCERT_TITLE);
                String formattedDate = concertJsonObject.getString(CONCERT_FORMATTED_DATETIME);
                String formattedLocation = concertJsonObject.getString(CONCERT_FORMATTED_LOCATION);
                String ticketUrl = concertJsonObject.getString(CONCERT_TICKET_URL);
                String ticketType = concertJsonObject.getString(CONCERT_TICKET_TYPE);
                String ticketStatus = concertJsonObject.getString(CONCERT_TICKET_STATUS);
                String description = concertJsonObject.getString(CONCERT_DESCRIPTION);

                //artist info
                JSONArray artistsJsonArray = concertJsonObject.getJSONArray(ARTISTS_ARRAY);
                JSONObject firstArtistJsonObject = artistsJsonArray.getJSONObject(0);
                String artistName = firstArtistJsonObject.getString(ARTIST_NAME);
                String artistImage = firstArtistJsonObject.getString(ARTIST_IMAGE);
                String artistWebsite = firstArtistJsonObject.getString(ARTIST_WEBSITE);

                //venue info
                JSONObject venue = concertJsonObject.getJSONObject(VENUE_OBJECT);
                String venueName = venue.getString(VENUE_NAME);
                String venuePlace = venue.getString(VENUE_PLACE);
                String venueCity = venue.getString(VENUE_CITY);
                String venueCountry = venue.getString(VENUE_COUNTRY);
                String venueLongitude = venue.getString(VENUE_LONGITUDE);
                String venueLatitude = venue.getString(VENUE_LATITUDE);

                //create a Concert Object out of the response
                Concert parsedConcert = new Concert(
                        title,
                        formattedDate,
                        formattedLocation,
                        ticketUrl,
                        ticketType,
                        ticketStatus,
                        description,
                        artistName,
                        artistImage,
                        artistWebsite,
                        venueName,
                        venuePlace,
                        venueCity,
                        venueCountry,
                        venueLongitude,
                        venueLatitude);

                //add the Concert Object to the List of Concerts
                mConcertList.add(parsedConcert);
            }
        }
        //return the List of Concerts, or null
        return (mConcertList.size() > 0) ?  mConcertList : null;
    }

}

