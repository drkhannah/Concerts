package com.drkhannah.concerts;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.drkhannah.concerts.R.string.extra_artist_name;

/**
 * Created by dhannah on 2/28/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //explicit intent to start the ConcertsService
        Intent concertsServiceIntent = new Intent(context, ConcertsService.class);
        concertsServiceIntent.putExtra(context.getString(extra_artist_name), Utils.getSharedPrefsArtistName(context));
        context.startService(concertsServiceIntent);
        showNotification(context);
    }

    private void showNotification(Context context) {
        //define the notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle(context.getString(R.string.alarm_notification_title))
                .setContentText(context.getString(R.string.alarm_notification_description, Utils.getSharedPrefsArtistName(context)));

        //intent to launch MainActivity class when notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        //wrap Intent in a Pending Intent
        //FLAG_UPDATE_CURRENT - Flag indicating that if the described PendingIntent already exists,
        //then keep it but replace its extra data with what is in this new Intent.
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Set Notification's click behavior
        notificationBuilder.setContentIntent(pendingIntent);

        //issue the notification
        // Sets an ID for the notification
        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());
    }
}
