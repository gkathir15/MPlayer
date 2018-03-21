package com.guru.mplayer.services;

import android.app.Notification;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.guru.mplayer.R;
import com.guru.mplayer.data_model.Music_Data;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public MusicService() {
    }

    private final IBinder iBinder = new LocalBinder();
    MediaPlayer mediaPlayer;
    String mediaID;
    String TAG = "musicService";
    int resumePos;
    public int position;
    String MediaID;
    ArrayList<Music_Data> musicList = new ArrayList<>();
    public static boolean IS_PLAYING = false;
    int mCurrentDuration;
    int mSongsListSize;
    boolean IS_PREPARED = false;
    int id = 5;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
//        position = intent.getIntExtra("position", 0);
//        musicList = (ArrayList<Music_Data>) intent.getSerializableExtra("songsList");
//        mediaID = musicList.get(position).getId();
//        mSongsListSize = musicList.size();
//        Log.d(TAG, mediaID);
//        initMediaPlayer();
//
//
 return super.onStartCommand(intent, flags, startId);
//        return Service.START_NOT_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        position = intent.getIntExtra("position", 0);
        musicList = (ArrayList<Music_Data>) intent.getSerializableExtra("songsList");
        mediaID = musicList.get(position).getId();
        mSongsListSize = musicList.size();
        Log.d(TAG, mediaID);
        initMediaPlayer();
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public void onCompletion(MediaPlayer mp) {

       Log.d(TAG,"Current song completed");
       IS_PLAYING =false;
        playNext();

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        mediaPlayer.reset();
        Toast.makeText(getApplicationContext(),"Media player error"+what,Toast.LENGTH_LONG).show();
        return false;
    }



    @Override
    public void onPrepared(MediaPlayer mp) {

        IS_PREPARED =true;




    }

    public void pause() {
        if (IS_PLAYING) {
           mCurrentDuration = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();

            IS_PLAYING =false;
            mediaPlayer.reset();
             }
    }

    public void playOnPause() {
        if (!IS_PLAYING) {
//
            playOnPauseFromDuration(mCurrentDuration);
        }
    }


    public void initMediaPlayer() {
        Log.d(TAG, "Initmedia");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();
        try {

            Log.d("MediaID to URi", mediaID);
            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(musicList.get(position).getId())));

        } catch (IOException e) {
            Log.d(TAG, "Crashed while Setting uri");
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                              @Override
                                              public void onPrepared(MediaPlayer mp) {

                                                 // mp.start();
                                                  mediaPlayer.start();
                                                  IS_PLAYING =true;

                                              }
                                          }
        );
        //mediaPlayer.start();
    }

    public void seekToDuration(final int duration)
    {

        mediaPlayer.reset();
        try {
            Log.d(TAG, mediaID);
            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(mediaID)));

        } catch (IOException e) {
            Log.d(TAG, "Crashed while Setting uri");
        }



        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.seekTo(duration);


                Log.d("seekduration", String.valueOf(duration));
//                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    @Override
//                    public void onSeekComplete(MediaPlayer mp) {
//                        mediaPlayer.start();
//                    }
//                });
                mediaPlayer.start();

                // mediaPlayer.start();
                IS_PLAYING=true;
            }
        });

    }

    public void playOnPauseFromDuration(final int duration)
    {

        mediaPlayer.reset();
        try {
            Log.d(TAG, mediaID);
            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(mediaID)));

        } catch (IOException e) {
            Log.d(TAG, "Crashed while Setting uri");
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.seekTo(duration);
//                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    @Override
//                    public void onSeekComplete(MediaPlayer mp) {
//                        mediaPlayer.start();
//                    }
//                });
                mediaPlayer.start();
                Log.d(TAG, String.valueOf(duration));
                // mediaPlayer.start();
                IS_PLAYING=true;
            }
        });

    }


    public void playNext()
    {
        if (position == mSongsListSize-1)
        {
            position = 0;
        }
        else {
            position = position + 1;
        }


        mediaID = musicList.get(position).getId();
        mediaPlayer.reset();
        try {
            Log.d(TAG, mediaID);
            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(mediaID)));

        } catch (IOException e) {
            Log.d(TAG, "Crashed while Setting uri");
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mediaPlayer.start();

                // mediaPlayer.start();
                IS_PLAYING=true;
            }
        });

    }

    public void playPrev()
    {

        Log.d("position", String.valueOf(position));
        Log.d("size of list", String.valueOf(mSongsListSize));

        if (position == 0)
        {
            position = mSongsListSize-1;
        }
        else
            position = position-1;


        mediaID = musicList.get(position).getId();
        mediaPlayer.reset();
        try {
            Log.d("MediaID to URi", mediaID);
            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(mediaID)));

        } catch (IOException e) {
            Log.d(TAG, "Crashed while Setting uri");
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mediaPlayer.start();

                // mediaPlayer.start();
                IS_PLAYING=true;
            }
        });

    }


    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public int getElapsedTime()
    {
        Log.d("currpos", String.valueOf(mediaPlayer.getCurrentPosition()));
       return mediaPlayer.getCurrentPosition();

    }

    public int getSongDuration()
    {
        return  mediaPlayer.getDuration();

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



//    private void setNotification() {
//        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, getPackageName())
//                .setContentText("Music servioce")
//                .setContentTitle("Music Service")
//                .setSmallIcon(R.drawable.ic_acoustic_guitar)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true);
//
//
//    }



}
