package com.drkhannah.concerts;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dhannah on 11/7/16.
 */

public class GetConcertsTask extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = GetConcertsTask.class.getSimpleName();

    private TextView mMainTextView;
    private Context mContext;

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
    protected String doInBackground(String... params) {

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
    protected void onPostExecute(String result) {
        mMainTextView.setText(result);
    }

    // Build a URL to request concerts for an artist
    private String downloadConcerts(String artistToSearch) throws IOException {

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

            //convert the buffer to a string
            String concertsJsonStr = buffer.toString();
            if (buffer.length() == 0) {
                // nothing in the StringBuffer so return null
                return null;
            }

            Log.d(LOG_TAG, "RESPONSE FROM BANDSINTOWN: " + concertsJsonStr);
            return concertsJsonStr;

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
}

