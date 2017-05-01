package com.drkhannah.concerts;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.drkhannah.concerts.data.ConcertsContract;
import com.squareup.picasso.Picasso;

/**
 * Created by dhannah on 5/1/17.
 */

public class ConcertsAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

            String artist = Utils.getSharedPrefsArtistName(context);

            Cursor cursor = context.getContentResolver().query(
                    ConcertsContract.ConcertEntry.buildConcertListForArtistUri(artist),
                    null,
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                views.setTextViewText(R.id.artist_name, artist);

                final String ticketStatus = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS));

                //set ticket icon type
                if (ticketStatus.equalsIgnoreCase("available")) {
                    Picasso.with(context).load(R.drawable.tickets_available).into(views, R.id.tickets_icon, appWidgetIds);
                } else {
                    Picasso.with(context).load(R.drawable.tickets_unavailable).into(views, R.id.tickets_icon, appWidgetIds);
                }

                views.setTextViewText(R.id.location, cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION)));
                views.setTextViewText(R.id.availability, cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS)));

                cursor.close();
            }


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
