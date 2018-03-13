package com.guru.mplayer.services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.guru.mplayer.data_model.Music_Data;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener {
    public MusicService() {
    }

    private final IBinder iBinder = new LocalBinder();
    MediaPlayer mediaPlayer;
    String mediaID;
    String TAG = "musicService";
    int resumePos;
    int position;
    String getMediaID;
    ArrayList<Music_Data> musicList = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"onStartCommand");
        position=  intent.getIntExtra("position",0);
        musicList = (ArrayList<Music_Data>) intent.getSerializableExtra("songsList");
        mediaID = musicList.get(position).getId();
        Log.d(TAG,mediaID);
        initMediaPlayer();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {


    }

    public boolean pause()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            return  true;
        }
        return false;
    }



    public void initMediaPlayer()
    {
        Log.d(TAG,"Initmedia");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();
        try {
            Log.d(TAG,mediaID);
            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(mediaID)));

        } catch (IOException e) {
            Log.d(TAG,"Crashed while Setting uri");
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                              @Override
                                              public void onPrepared(MediaPlayer mp) {
                                                  mp.start();

                                              }
                                          }
        );
        //mediaPlayer.start();
    }



    public class LocalBinder extends Binder {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mediaPlayer.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        mediaPlayer.release();

        return super.onUnbind(intent);
    }
}
