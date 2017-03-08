package sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by dhannah on 3/8/17.
 */

public class AuthenticatorService extends Service {

    private ConcertsAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        //new instance of our ConcertsAuthenticator class
        mAuthenticator = new ConcertsAuthenticator(this);
    }

    //When the Android System binds to this Service return the authenticator's IBinder.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
