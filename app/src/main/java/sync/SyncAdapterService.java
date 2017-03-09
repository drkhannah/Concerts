package sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by dhannah on 3/8/17.
 */

public class SyncAdapterService extends Service {

    private static ConcertsSyncAdapter sConcertsSyncAdapter = null;

    //thread-safe Lock
    private static final Object sConcertsSyncAdapterLock = new Object();


    @Override
    public void onCreate() {
        //use thread-safe lock object to create a singleton instance
        //of the ConcertsSyncAdatper
        synchronized (sConcertsSyncAdapterLock) {
            if (sConcertsSyncAdapter == null) {
                sConcertsSyncAdapter = new ConcertsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return the object that allows calls to onPerformSync()
        //and invoke the ConcertsSyncAdapter
        return sConcertsSyncAdapter.getSyncAdapterBinder();
    }
}
