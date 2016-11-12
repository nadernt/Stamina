package com.fleecast.stamina.backup;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

/**
 * Async task for getting user account info
 */
class CreateDropBoxFolderStruct extends AsyncTask<String, Void, Void> {

    private final DbxClientV2 mDbxClient;
   // private final Callback mCallback;
    private Exception mException;

    @Override
    protected Void doInBackground(String... strings) {
        try {
            mDbxClient.files().listFolder(strings[0]);
            //mDbxClient.files().createFolder(strings[0]);
            //mDbxClient.files().delete(strings[0]);
            //mDbxClient.auth().tokenRevoke();
            //mDbxClient.auth().
        } catch (DbxException e) {
            Log.e("jjjj","hhhhhhh");
            e.printStackTrace();
        }
        return null;
    }

    public interface Callback {
        void onComplete(FullAccount result);
        void onError(Exception e);
    }

    CreateDropBoxFolderStruct(DbxClientV2 dbxClient) {
        mDbxClient = dbxClient;

    }

/*
    @Override
    protected void onPostExecute(FullAccount account) {
        super.onPostExecute(account);
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onComplete(account);
        }
    }
*/

  /*  @Override
    protected FullAccount doInBackground(Void... params) {

        try {
            return mDbxClient.users().getCurrentAccount();

        } catch (DbxException e) {
            mException = e;
        }

        return null;
    }*/
}
