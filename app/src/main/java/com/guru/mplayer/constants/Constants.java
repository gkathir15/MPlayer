package com.guru.mplayer.constants;

import android.net.Uri;
import android.provider.MediaStore;

import java.net.URI;

/**
 * Created by Guru on 09-03-2018.
 */

public class Constants {

    public static Uri MEDIA_AUDIO_URI = Uri.parse("MediaStore.Audio.Media.EXTERNAL_CONTENT_URI");

    public static String[] AUDIO_PROJECTION_META = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
    };

}
