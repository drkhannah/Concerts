package com.drkhannah.concerts;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dhannah on 2/9/17.
 */

public class Utils {

    public static String getSharedPrefsArtistName(Context context) {
        //get the artist name saved in the com.drkhannah.concerts.CONCERTS_SHARED_PREFERENCE_FILE Shared Preferences file
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.shared_prefs_artist_name), context.getString(R.string.default_artist_name));
    }

    public static void saveSharedPrefsArtistName(Context context, String artistName) {
        //Use SharedPreferences.Editor to save artist name to the
        //com.drkhannah.concerts.CONCERTS_SHARED_PREFERENCE_FILE Shared Preferences file
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
        sharedPrefsEditor.putString(context.getString(R.string.shared_prefs_artist_name), artistName.toLowerCase());
        sharedPrefsEditor.commit();
    }
}
