package com.drkhannah.concerts;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dhannah on 2/9/17.
 */

public class Utils {

    public static final String KEY_PREF_SYNC = "sync_interval";

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

    public static long getSyncInterval(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return TimeUnit.DAYS.toSeconds(sharedPreferences.getInt(KEY_PREF_SYNC, 1));
    }

    public static void updateAppWidget(Context context) {
        //update the app widget
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ConcertsAppWidgetProvider.class));
        Intent intent = new Intent(context, ConcertsAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }
}
