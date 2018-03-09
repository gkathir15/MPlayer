package com.guru.mplayer.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.guru.mplayer.constants.Constants;
import com.guru.mplayer.data_model.Music_Data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Guru on 09-03-2018.
 */

public class MediaDataHelper {
    Music_Data musicData = new Music_Data();




    public void queryMediaMeta(Context context)
    {
        //CursorLoader lCursorLoader = new CursorLoader(context, Constants.MEDIA_AUDIO_URI,Constants.AUDIO_PROJECTION_META,null,null,null);

        Cursor mCursor = context.getContentResolver().query( Constants.MEDIA_AUDIO_URI,Constants.AUDIO_PROJECTION_META,null,null,null);
        mCursor.moveToFirst();
        ArrayList<Music_Data> TracksList = new ArrayList<Music_Data>();
        // Music Data constructor Signature  Music_Data(String trackURI, String title, String length, String albumName)
         do {






         }while (mCursor.isLast());
    }


}
