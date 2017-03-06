package com.drkhannah.concerts;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import static com.drkhannah.concerts.R.string.extra_artist_name;

/**
 * Created by dhannah on 3/3/17.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ConcertsJobService extends JobService {

    private BroadcastReceiver mJobFinishedBroadcastReceiver;
    private JobParameters mJobParameters;

    @Override
    public boolean onStartJob(JobParameters params) {
        mJobParameters = params;

        //explicit intent to start the ConcertsService
        Intent concertsServiceIntent = new Intent(getApplicationContext(), ConcertsService.class);
        concertsServiceIntent.putExtra(getApplicationContext().getString(extra_artist_name), Utils.getSharedPrefsArtistName(getApplicationContext()));
        getApplicationContext().startService(concertsServiceIntent);

        //broadcast receiver for jobFinished broadcast from ConcertsService
        mJobFinishedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                jobFinished(mJobParameters, false);
                showNotification();
            }
        };

        //register for mEmptyTextView local broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mJobFinishedBroadcastReceiver, new IntentFilter(getString(R.string.job_finished_action)));

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //unregister from mEmptyTextView local broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mJobFinishedBroadcastReceiver);

        //return true because we want to reschedule
        //this job if it was stopped
        return true;
    }

    private void showNotification() {
        //define the notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle(getApplicationContext().getString(R.string.job_notification_title))
                .setContentText(getApplicationContext().getString(R.string.job_notification_description));

        //intent to launch MainActivity class when notification is clicked
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //wrap Intent in a Pending Intent
        //FLAG_UPDATE_CURRENT - Flag indicating that if the described PendingIntent already exists,
        //then keep it but replace its extra data with what is in this new Intent.
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Set Notification's click behavior
        notificationBuilder.setContentIntent(pendingIntent);

        //issue the notification
        // Sets an ID for the notification
        int mNotificationId = 2;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());
    }

}
