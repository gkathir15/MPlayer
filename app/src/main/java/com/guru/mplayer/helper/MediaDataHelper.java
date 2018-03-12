package com.guru.mplayer.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.guru.mplayer.constants.Constants;
import com.guru.mplayer.data_model.Music_Data;

import java.util.ArrayList;

/**
 * Created by Guru on 09-03-2018.
 */

public class MediaDataHelper {

    Cursor mCursor;


    Music_Data musicData = new Music_Data();




    public ArrayList<Music_Data> queryMediaMeta(Context context)
    {
        //CursorLoader lCursorLoader = new CursorLoader(context, Constants.MEDIA_AUDIO_URI,Constants.AUDIO_PROJECTION_META,null,null,null);
       // ContentResolver contentResolver = context.getContentResolver();
         mCursor = context.getContentResolver().query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM
    },null,null,null);

        ArrayList<Music_Data> TracksList = new ArrayList<>();
        Log.d("track list Size", String.valueOf(TracksList.size()));

        try {

               if(mCursor.moveToFirst()) {
                   Log.d("cursorfirst", "moveto first");

                   // Music Data constructor Signature  Music_Data(String trackURI, String title, String length, String albumName)
                   do {


                       new Music_Data(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media._ID))
                               , mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                               , mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                               , mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                       TracksList.add(musicData);
                       Log.d(musicData.getTitle(), "music");


                   } while (mCursor.moveToNext());
               }

               else{
                   Log.d("cursor ","cursor not moved to first");
               }
        }
        finally {
            Log.d("track list Size", String.valueOf(TracksList.size()));


            mCursor.close();

        }

        return  TracksList;
    }


    //public void


}
