package com.guru.mplayer.constants;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.net.URI;

/**
 * Created by Guru on 09-03-2018.
 */

public class Constants {

    public static class MediaUri implements BaseColumns{

    public static Uri MEDIA_AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public static String Title = "MediaStore.Audio.Media.TITLE";
    public static String Id = "MediaStore.Audio.Media._ID";
    public static String Duration = "MediaStore.Audio.Media.DURATION";
    public static String AlbumID = "MediaStore.Audio.Media.ALBUM_ID";

    public static String[] AUDIO_PROJECTION_META = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Albums.ALBUM_ID
    };
        }

}
