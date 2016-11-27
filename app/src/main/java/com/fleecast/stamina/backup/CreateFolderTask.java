package com.fleecast.stamina.backup;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.SearchResult;
import com.dropbox.core.v2.users.FullAccount;

/**
 * Async task for getting user account info
 */
class CreateFolderTask extends AsyncTask<String, Void, Void> {

    private final DbxClientV2 mDbxClient;
   // private final Callback mCallback;
    private Exception mException;

    @Override
    protected Void doInBackground(String... strings) {
        try {
           // SearchResult searchResult = mDbxClient.files().search(strings[0],"stamina");
            //mDbxClient.files().delete(strings[0]);
            //mDbxClient.files().listFolder(strings[0]);
            // Get files and folder metadata from Dropbox root directory
            ListFolderResult result = mDbxClient.files().listFolder(strings[0]);
/*
            if (item instanceof FileMetadata) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ext = item.getName().substring(item.getName().indexOf(".") + 1);
                String type = mime.getMimeTypeFromExtension(ext);
                if (type != null && type.startsWith("image/")) {

                }
*/
            while (true) {

                for (Metadata metadata : result.getEntries()) {
                    System.out.println(metadata.getPathLower());
                    System.out.println(metadata.getName());
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = mDbxClient.files().listFolderContinue(result.getCursor());
            }


            //mDbxClient.auth().
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Callback {
        void onComplete(FullAccount result);
        void onError(Exception e);
    }

    CreateFolderTask(DbxClientV2 dbxClient) {
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
