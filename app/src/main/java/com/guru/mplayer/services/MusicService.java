package com.guru.mplayer.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.guru.mplayer.R;
import com.guru.mplayer.activities.MainActivity;
import com.guru.mplayer.data_model.MusicData;
import com.guru.mplayer.helper.CommonHelper;

import java.io.IOException;
import java.util.ArrayList;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;


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
    ArrayList<MusicData> musicList = new ArrayList<>();
    public static boolean IS_PLAYING = false;
    int mCurrentDuration;
    int mSongsListSize;
    boolean IS_PREPARED = false;
    int id = 5;
    public static String COMPLETION_CAST = "TRACK_COMPLETED";
    int lTempPos = 0;
    int NOTIFICATION_ID = 5;
    String CHANNEL_ID ="MUSIC";
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackStateCompat;
    private MediaSessionManager mMediaSessionManager;
    private  MediaControllerCompat mediaController;
    private  MediaDescriptionCompat mediaDescription;
    private  MediaMetadataCompat mediaMetadata;
    NotificationManager notificationManager;
    CommonHelper commonHelper = new CommonHelper();








    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
        position = intent.getIntExtra("position", 0);
        position = intent.getIntExtra("position", 0);
        musicList = (ArrayList<MusicData>) intent.getSerializableExtra("songsList");
        mediaID = musicList.get(position).getId();
        mSongsListSize = musicList.size();
        Log.d(TAG, mediaID);
        if (!mediaPlayer.isPlaying())
        initMediaPlayer();
        else
            playAtPos(position);

      //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

//        position = intent.getIntExtra("position", 0);
//        musicList = (ArrayList<MusicData>) intent.getSerializableExtra("songsList");
//        mediaID = musicList.get(position).getId();
//        mSongsListSize = musicList.size();
//        Log.d(TAG, mediaID);
//        initMediaPlayer();
//        Log.d("mListSize", String.valueOf(musicList.size()));
//        Log.d("mPosition", String.valueOf(position));
        return iBinder;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Oncreate Called");
        mediaPlayer = new MediaPlayer();
//        mMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
//        mMediaSessionCompat = new MediaSessionCompat(this,TAG);
//        mediaController = mMediaSessionCompat.getController();
//        mediaMetadata = mediaController.getMetadata();
//        mediaDescription = mediaMetadata.getDescription();
//        //setSessionToken(mMediaSessionCompat.getMediaSession(),mMediaSessionCompat.getSessionToken());
//        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS|MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//        mPlaybackStateCompat = new PlaybackStateCompat.Builder()
//                                .setActions(PlaybackStateCompat.ACTION_PAUSE|PlaybackStateCompat.ACTION_PLAY);
//        mMediaSessionCompat.setPlaybackState(mPlaybackStateCompat.build());
//
//
//        mMediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
//            @Override
//            public void onPlay() {
//                super.onPlay();
//                playOnPause();
//            }
//
//            @Override
//            public void onPause() {
//                super.onPause();
//                pause();
//            }
//
//        });






    }

    @Override
    public void onCompletion(MediaPlayer mp) {

       Log.d(TAG,"Current song completed");
       IS_PLAYING =false;
        playNext();
        Intent intent = new Intent(COMPLETION_CAST);
        intent.putExtra("position",position);
        intent.putExtra("status","playing next");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("completed","Broadcast fired");
//        commonHelper.notificationBuilder.setContentTitle(musicList.get(position).getAlbumName());
//        commonHelper.notificationBuilder.setContentText(musicList.get(position).getTitle());
//        notificationManager.notify();



    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        mediaPlayer.reset();
       // Toast.makeText(getApplicationContext(),"Media player error"+what,Toast.LENGTH_LONG).show();
        Log.d("MediaError","Media player error"+what);
        return false;
    }



    @Override
    public void onPrepared(MediaPlayer mp) {

        IS_PREPARED =true;




    }

    public void pause() {
        if (IS_PLAYING) {
//           mCurrentDuration = mediaPlayer.getCurrentPosition();
            IS_PLAYING =false;
            mediaPlayer.pause();


//            mediaPlayer.reset();
             }
//        mediaPlayer.pause();
//        IS_PLAYING =false;
    }

    public void playOnPause() {
        if (!IS_PLAYING) {
            IS_PLAYING = true;
            mediaPlayer.start();

//
           // playOnPauseFromDuration(mCurrentDuration);
        }
    }


    public void initMediaPlayer() {
        Log.d(TAG, "Initmedia");
//        if (mediaPlayer.isPlaying())
//        {
//            mediaPlayer.reset();
//            mediaPlayer = null;
//        }
       // mediaPlayer.release();

        //mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);

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
                                                  buildNotification();

                                              }
                                          }
        );

        //initSession();
       // buildNotification();
    }

    public void seekToDuration(final int duration)
    {
        mediaPlayer.seekTo(duration);
        IS_PLAYING=true;

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
                mediaPlayer.start();
                Log.d(TAG, String.valueOf(duration));
                // mediaPlayer.start();
                IS_PLAYING=true;
            }
        });

    }


    public void playNext()
    {
        if (position == musicList.size()-1)
        {
            position = 0;
        }
        else {
            ++position;
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
            position = --mSongsListSize;
        }
        else
        {
            position = --position;}


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

    public void playAtPos(int position)
    {



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
        notificationManager.cancelAll();

    }

    public int getElapsedTime()
    {

        if (mediaPlayer.isPlaying())
        {
            lTempPos = mediaPlayer.getCurrentPosition();
        Log.d("currpos", String.valueOf(lTempPos));
       return lTempPos;
        }

       else
        {
            return lTempPos;
        }

    }

    public int getSongDuration()
    {
        return  mediaPlayer.getDuration();

    }





    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public boolean onUnbind(Intent intent) {


        return super.onUnbind(intent);
    }

    public boolean isIsPlaying()
    {
        return mediaPlayer.isPlaying();
    }


    private void buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Music",IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);
//
//        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
////                .setMediaSession(mMediaSessionCompat.getSessionToken())
////                .setShowActionsInCompactView(0))
////                .setLargeIcon(mediaMetadata.getBitmap(TAG))
////                .setContentTitle(mediaDescription.getTitle())
////                .setContentText(mediaDescription.getTitle())
//                .setContentTitle(musicList.get(position).getAlbumName())
//                .setContentText(musicList.get(position).getTitle())
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.drawable.guitarbg)
//                .setAutoCancel(false)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//              // .addAction(R.drawable.play,"play", MediaButtonReceiver.buildMediaButtonPendingIntent(this,))
//                //.addAction(R.drawable.play,)
//
//                .build();


//     // notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//            notificationManager.notify(id,commonHelper.setNotification(CHANNEL_ID,musicList.get(position).getAlbumName(),musicList.get(position).getTitle(),this,pendingIntent));
//        }
//        else
        startForeground(id,commonHelper.setNotification(CHANNEL_ID,musicList.get(position).getAlbumName(),musicList.get(position).getTitle(),this,pendingIntent));

    }







}
