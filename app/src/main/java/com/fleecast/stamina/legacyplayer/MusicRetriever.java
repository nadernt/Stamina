/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fleecast.stamina.legacyplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Retrieves and organizes media to play. Before being used, you must call ,
 * which will retrieve all of the music on the user's device (by performing a query on a content
 * resolver). After that, it's ready to retrieve a random song, with its title and URI, upon
 * request.
 */
public class MusicRetriever {
    final String TAG = "MusicRetriever";
    private static Context context;
    private MyApplication myApplication;

    ContentResolver mContentResolver;

    // the items (songs) we have queried
    Item mItems;

    public MusicRetriever(Context context) {
        this.context = context;
        /*String fileName = getIntent().getStringExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER);
        playListHelper.loadJustSingleFileForPlay(fileName, dbId);*/
        myApplication = (MyApplication)context.getApplicationContext();
    }



    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /** Returns a random Item. If there are no items available, returns null. */
    public Item getItem() {

        myApplication.setIsPlaying(false);

/*
        if (myApplication.getIndexSomethingIsPlaying()  > myApplication.stackPlaylist.size()-1) {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying());
            return null;
        }

        if (myApplication.getIndexSomethingIsPlaying() < 0 )
        {
            myApplication.setIndexSomethingIsPlaying(0);
            return null;
        }
*/

        return new Item(myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getId(),
                myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getTitle(),
                myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getDescription(),null,0);
    }

    /** Returns a random Item. If there are no items available, returns null. */
    public void getNextItem() {

        if (myApplication.getIndexSomethingIsPlaying() < myApplication.stackPlaylist.size()-1) {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying() + 1);
        }
    }


    /** Returns a random Item. If there are no items available, returns null. */
    public void getPrevItem() {

        if (myApplication.getIndexSomethingIsPlaying() > 0 )
        {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying()-1);
        }

    }


    public static class Item {
        long id;
        String artist;
        String title;
        String album;
        long duration;
        private final MyApplication myApplication;

        public Item(long id, String artist, String title, String album, long duration) {
            myApplication = (MyApplication)context.getApplicationContext();

            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
        }

        public long getId() {
            return id;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        public long getDuration() {
            return duration;
        }

        public Uri getURI() {
            return Uri.parse(myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getFileName());
        }
    }
}
