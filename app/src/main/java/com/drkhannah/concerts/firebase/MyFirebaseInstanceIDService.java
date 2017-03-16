package com.drkhannah.concerts.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by dhannah on 3/16/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String LOG_TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    //constructor
    public MyFirebaseInstanceIDService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        //get updated ID token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken);
    }
}
