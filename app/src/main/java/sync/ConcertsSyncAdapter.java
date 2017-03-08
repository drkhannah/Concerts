package sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.drkhannah.concerts.R;

/**
 * Created by dhannah on 3/8/17.
 */

public class ConcertsSyncAdapter extends AbstractThreadedSyncAdapter {

    private ContentResolver mContentResolver;

    public ConcertsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    //second form of constructor maintains compatibility with Android 3.0 and later
    public ConcertsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }

    //create sync account
    public static Account CreateSyncAccount(Context context) {
        //create account
        Account newAccount = new Account(context.getString(R.string.dummy_account), context.getString(R.string.account_type));

        //get AccountManager System Service
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        //add account and account type
        //return Account object or report error
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

            //if you don't set android:syncable="true" in the <provider> element
            //in the Manifest, then call context.setIsSyncable(account, AUTHORITY, 1); here
            return newAccount;
        } else {
            //the account already exists, or some other error has occur
            return null;
        }
    }
}
