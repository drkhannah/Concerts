package com.drkhannah.concerts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.drkhannah.concerts.R;
import com.drkhannah.concerts.Utils;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by dhannah on 3/8/17.
 */

public class ConcertsSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = ConcertsSyncAdapter.class.getSimpleName();

    public static final long SYNC_FLEXTIME = TimeUnit.HOURS.toSeconds(1);

    //constructor
    public ConcertsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    //second form of constructor maintains compatibility with Android 3.0 and later
    public ConcertsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    public static void initSyncAdapter(Context context) {
        //create account for ConcertsSyncAdapter
        createSyncAccount(context);
    }

    //create a dummy account for ConcertsSyncAdapter
    private static Account createSyncAccount(Context context) {
        // Get the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.dummy_account), context.getString(R.string.account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            //Add the account and account type, no password or user data
            // return the Account object, otherwise report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            //if you don't set android:syncable="true" in the <provider> element
            //in the Manifest, then call context.setIsSyncable(account, AUTHORITY, 1); here
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        //Since we've created an account
        long syncInterval = Utils.getSyncInterval(context);
        configurePeriodicSync(context, syncInterval, SYNC_FLEXTIME);

        //Without calling setSyncAutomatically, periodic com.drkhannah.concerts.sync will not be enabled.
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    }

    public static void configurePeriodicSync(Context context, long syncInterval, long flexTime) {
        Account account = createSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic com.drkhannah.concerts.sync
            // for KITKAT and greater
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncNow(Context context) {
        // Pass the settings flags by inserting them in a bundle
        //SYNC_EXTRAS_MANUAL - forces manual com.drkhannah.concerts.sync
        //SYNC_EXTRAS_EXPEDITED - starts com.drkhannah.concerts.sync immediately so that the system doesn't wait to run they com.drkhannah.concerts.sync adapter
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Account account = createSyncAccount(context);
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), settingsBundle);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //get the artist name from SharedPreferences
        String artistName = Utils.getSharedPrefsArtistName(getContext());

        //dont make network call if artist is in database already
        //and they were updated within the last 24 hours
        if (!checkArtistTimestamp(artistName)) {
            Utils.updateAppWidget(getContext());
            //Log.d(LOG_TAG, "artist searched within the last 24 hours");
            return;
        }


        // Will contain the raw JSON response as a string.
        String concertsJsonStr = null;

        // Declared outside try/catch so they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            //Build a uri to construct a valid url
            final String BASE_URL = getContext().getString(R.string.base_url);
            final String RESPONSE_FORMAT = getContext().getString(R.string.response_format_param);
            final String API_VERSION = getContext().getString(R.string.api_version_param);
            final String APP_ID = getContext().getString(R.string.app_id_param);

            //build a valid URI
            Uri validUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(artistName)
                    .appendPath(RESPONSE_FORMAT)
                    .appendQueryParameter(API_VERSION, getContext().getString(R.string.api_version_value))
                    .appendQueryParameter(APP_ID, getContext().getString(R.string.app_id_value))
                    .build();

            //create a URL from the URI we built above
            URL url = new URL(validUri.toString());
            //Log.d(LOG_TAG, "URL for request: " + url);

            // Create a GET request to the BandsInTown api and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //Log.d(LOG_TAG, "Request Response Code: " + urlConnection.getResponseCode());
                //send local broadcast to ConcertListFragment
                sendEmptyTextViewLocalBroadcast(getContext().getString(R.string.no_such_artist, Utils.getSharedPrefsArtistName(getContext())));
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
                sendEmptyTextViewLocalBroadcast(getContext().getString(R.string.no_concerts_for, Utils.getSharedPrefsArtistName(getContext())));
                return;
            }

            //convert the stringBuilder to a string
            concertsJsonStr = stringBuilder.toString();

            if (concertsJsonStr.equalsIgnoreCase("[]\n")) {
                // nothing in the concertsJsonStr so return null
                //send local broadcast to ConcertListFragment
                sendEmptyTextViewLocalBroadcast(getContext().getString(R.string.no_concerts_for, Utils.getSharedPrefsArtistName(getContext())));
                return;
            }

            //Log.d(LOG_TAG, "RESPONSE FROM BANDSINTOWN: " + concertsJsonStr);

        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error ", e);
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
                    //Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            parseJson(concertsJsonStr);
        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage());
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
            JSONArray artistsJsonArray = concertJsonObject.getJSONArray(getContext().getString(R.string.response_object_key_artists));
            JSONObject firstArtistJsonObject = artistsJsonArray.getJSONObject(0);
            String artistName = firstArtistJsonObject.optString(getContext().getString(R.string.response_object_key_name), getContext().getString(R.string.no_artist_name_available));
            String artistImage = firstArtistJsonObject.optString(getContext().getString(R.string.response_object_key_thumb_url), getContext().getString(R.string.no_artist_image_available));
            String artistWebsite = firstArtistJsonObject.optString(getContext().getString(R.string.response_object_key_website), getContext().getString(R.string.no_artist_website_available));

            oldArtistId = checkArtistId(artistName);

            long newArtistId = insertArtist(artistName, artistImage, artistWebsite);

            //loop through concertsJsonArray to get all concerts for the artist
            for (int i = 0; i < concertsJsonArray.length(); i++) {
                //get a concert object from the response
                concertJsonObject = concertsJsonArray.getJSONObject(i);

                //concert object in response
                String title = concertJsonObject.optString(getContext().getString(R.string.response_object_key_title), getContext().getString(R.string.no_title_available));
                String dateTime = concertJsonObject.optString(getContext().getString(R.string.response_object_key_datetime), getContext().getString(R.string.no_date_available));
                String formattedDateTime = concertJsonObject.optString(getContext().getString(R.string.response_object_key_formatted_datetime), getContext().getString(R.string.no_date_available));
                String formattedLocation = concertJsonObject.optString(getContext().getString(R.string.response_object_key_formatted_location), getContext().getString(R.string.no_location_available));
                String ticketUrl = concertJsonObject.optString(getContext().getString(R.string.response_object_key_ticket_url), getContext().getString(R.string.no_ticket_url_available));
                String ticketType = concertJsonObject.optString(getContext().getString(R.string.response_object_key_ticket_type), getContext().getString(R.string.no_ticket_type_available));
                String ticketStatus = concertJsonObject.optString(getContext().getString(R.string.response_object_key_ticket_status), getContext().getString(R.string.no_ticket_status_available));
                String description = concertJsonObject.optString(getContext().getString(R.string.response_object_key_description), getContext().getString(R.string.no_description_available));

                //venue object in response
                JSONObject venue = concertJsonObject.getJSONObject(getContext().getString(R.string.response_object_key_venue));
                String venueName = venue.optString(getContext().getString(R.string.response_object_key_venue_name), getContext().getString(R.string.no_venue_name_available));
                String venuePlace = venue.optString(getContext().getString(R.string.response_object_key_place), getContext().getString(R.string.no_venue_place_available));
                String venueCity = venue.optString(getContext().getString(R.string.response_object_key_city), getContext().getString(R.string.no_venue_city_available));
                String venueRegion = venue.optString(getContext().getString(R.string.response_object_key_region), getContext().getString(R.string.no_venue_region_available));
                String venueCountry = venue.optString(getContext().getString(R.string.response_object_key_country), getContext().getString(R.string.no_venue_country_available));
                String venueLongitude = venue.optString(getContext().getString(R.string.response_object_key_longitude), getContext().getString(R.string.no_longitude_available));
                String venueLatitude = venue.optString(getContext().getString(R.string.response_object_key_latitude), getContext().getString(R.string.no_latitude_available));

                ContentValues concertValues = new ContentValues();

                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY, newArtistId);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_TITLE, title);
                concertValues.put(ConcertsContract.ConcertEntry.COLUMN_DATE_TIME, dateTime);
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
                getContext().getContentResolver().bulkInsert(ConcertsContract.ConcertEntry.CONTENT_URI, concertsArray);
            }

            if (oldArtistId > 0) {
                purgeOldConcerts(oldArtistId);
            }
            Utils.updateAppWidget(getContext());
        }
    }

    private int purgeOldConcerts(long oldArtistId) {
        String oldId = String.valueOf(oldArtistId);

        //delete all concerts records with an artist_id
        //that matches the old artist _id
        return getContext().getContentResolver().delete(
                ConcertsContract.ConcertEntry.CONTENT_URI,
                ConcertsContract.ConcertEntry.COLUMN_ARTIST_KEY + " = ?", //selection
                new String[]{oldId} //selectionArgs
        );
    }

    private long checkArtistId(String artistName) {
        long artistId = 0;

        // check if the artist with this name exists in the db
        Cursor artistCursor = getContext().getContentResolver().query(
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
        //close cursor
        if (artistCursor != null) {
            artistCursor.close();
        }
        return artistId;
    }

    private boolean checkArtistTimestamp(String artistName) {
        // check if the artist with this name exists in the db
        Cursor artistCursor = getContext().getContentResolver().query(
                ConcertsContract.ArtistEntry.CONTENT_URI, //URI
                new String[]{ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP}, //projection
                ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME + " = ?", //selection
                new String[]{artistName}, //selectionArgs
                null //sortOrder
        );

        if (artistCursor != null && artistCursor.moveToFirst()) {
            //find the column index number for the artist timestamp column
            int artistTimestampIndex = artistCursor.getColumnIndex(ConcertsContract.ArtistEntry.COLUMN_TIME_STAMP);
            //get the artist timestamp
            long artistTimestamp = artistCursor.getLong(artistTimestampIndex);
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - artistTimestamp;
            long oneDay = TimeUnit.DAYS.toMillis(1);
            artistCursor.close();
            return (timeDifference > oneDay);
        }

        //close cursor
        if (artistCursor != null) {
            artistCursor.close();
        }
        return true;
    }

    private long insertArtist(String artistName, String img_url, String website_url) {
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
        Uri insertedUri = getContext().getContentResolver().insert(
                ConcertsContract.ArtistEntry.CONTENT_URI,
                artistValues
        );

        // The resulting URI contains the _id for the row.  Extract the artist _id from the Uri.
        artistId = ContentUris.parseId(insertedUri);

        return artistId;
    }

    //sends local broadcast to update empty textview in ConcertListFragment
    private void sendEmptyTextViewLocalBroadcast(String string) {
        Intent emptyTextViewIntent = new Intent(getContext().getString(R.string.empty_text_action));
        emptyTextViewIntent.putExtra(getContext().getString(R.string.empty_text_view_extra), string);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(emptyTextViewIntent);
    }
}
