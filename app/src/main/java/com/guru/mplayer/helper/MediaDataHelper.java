package com.guru.mplayer.helper;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.guru.mplayer.data_model.AlbumData;
import com.guru.mplayer.data_model.Music_Data;

import java.util.ArrayList;

/**
 * Created by Guru on 09-03-2018.
 */

public class MediaDataHelper {

    Cursor mCursor, mAlbumCursor;


    public ArrayList<Music_Data> queryMediaMeta(Context context) {
        mCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        }, null, null, MediaStore.Audio.Media.ALBUM+" ASC" );


        ArrayList<Music_Data> TracksList = new ArrayList<>();
        Log.d("track list Size", String.valueOf(TracksList.size()));

        try {

            if (mCursor.moveToFirst()) {
                Log.d("cursorfirst", "moveto first");


                // public Music_Data(String id,int length, String title, String albumName, String albumArt)
                do {


                    Music_Data music_data = new Music_Data(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media._ID))
                            , mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            , mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                            , mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            , mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//
                    //                  music_data.setAlbumArt(mAlbumCursor.getString(mAlbumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                    // Log.d("albumArt",music_data.getAlbumArt());

                    Log.d("music cursor", mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                    TracksList.add(music_data);


                } while (mCursor.moveToNext());


            } else {
                Log.d("cursor ", "cursor not moved to first");


//
            }
        } finally {

            Log.d("track list Size", String.valueOf(TracksList.size()));


            mCursor.close();

        }

        return TracksList;
    }


}
