package com.drkhannah.concerts;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.drkhannah.concerts.adapters.ConcertsRecyclerViewAdapter;
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

    private ConcertsRecyclerViewAdapter mConcertsRecyclerViewAdapter;
    private List<Concert> mConcertList = new ArrayList<>();
    private Context mContext;


    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     * This receives a TextView and sets its text in onPostExecute()
     */
    public GetConcertsTask(Context context, ConcertsRecyclerViewAdapter concertsRecyclerViewAdapter) {
        super();
        mConcertsRecyclerViewAdapter = concertsRecyclerViewAdapter;
        mContext = context;
    }

    //creates a new thread
    @Override
    protected List<Concert> doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the artist.
        return downloadConcerts(params[0]);
    }

    // onPostExecute delivers the results of  doInBackground() on the UI thread.
    @Override
    protected void onPostExecute(List<Concert> result) {
        if (result != null) {
            mConcertsRecyclerViewAdapter.updateData(result);
        } else {
            mConcertsRecyclerViewAdapter.updateData(null);
        }
    }

    // Build a URL to request concerts for an artist
    private List<Concert> downloadConcerts(String artistToSearch) {

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

            //return a parsed response List<Concert>
            Log.d(LOG_TAG, "RESPONSE FROM BANDSINTOWN: " + concertsJsonStr);
            return parseJson(concertsJsonStr);

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
    }

    private List<Concert> parseJson(String concertsJsonStr) {

        try {
            //concertsJsonStr starts with a jsonArray
            final JSONArray concertsJsonArray = new JSONArray(concertsJsonStr);

            if (concertsJsonArray.length() > 0) {
                //loop through concertsJsonArray
                for (int i = 0; i < concertsJsonArray.length(); i++) {
                    //get a concert object from the response
                    JSONObject concertJsonObject = concertsJsonArray.getJSONObject(i);

                    //concert object in response
                    String title = concertJsonObject.optString(mContext.getString(R.string.response_object_key_title), mContext.getString(R.string.no_title_available));
                    String formattedDate = concertJsonObject.optString(mContext.getString(R.string.response_object_key_formatted_datetime), mContext.getString(R.string.no_date_available));
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
                    String venueCountry = venue.optString(mContext.getString(R.string.response_object_key_country), mContext.getString(R.string.no_venue_country_available));
                    String venueLongitude = venue.optString(mContext.getString(R.string.response_object_key_longitude), mContext.getString(R.string.no_longitude_available));
                    String venueLatitude = venue.optString(mContext.getString(R.string.response_object_key_latitude), mContext.getString(R.string.no_latitude_available));

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
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error parsing response string", e);
        }
        //return the List of Concerts, or null
        return (mConcertList.size() > 0) ? mConcertList : null;
    }
}

