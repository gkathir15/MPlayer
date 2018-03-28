package com.guru.mplayer.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.guru.mplayer.R;
import com.guru.mplayer.activities.MainActivity;
import com.guru.mplayer.activities.PlayerActivity;
import com.guru.mplayer.data_model.MusicData;

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






    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
        position = intent.getIntExtra("position", 0);
        musicList = (ArrayList<MusicData>) intent.getSerializableExtra("songsList");
        mediaID = musicList.get(position).getId();
        mSongsListSize = musicList.size();
        Log.d(TAG, mediaID);
        initMediaPlayer();

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

                                              }
                                          }
        );
        buildNotification();
        //initSession();
       // buildNotification();
    }

    public void seekToDuration(final int duration)
    {

//        mediaPlayer.reset();
//        try {
//            Log.d(TAG, mediaID);
//            mediaPlayer.setDataSource(getApplicationContext(), ContentUris.withAppendedId(
//                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    Long.parseLong(mediaID)));
//
//        } catch (IOException e) {
//            Log.d(TAG, "Crashed while Setting uri");
//        }
//
//
//
//        mediaPlayer.prepareAsync();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer.seekTo(duration);
//
//
//                Log.d("seekduration", String.valueOf(duration));
////                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
////                    @Override
////                    public void onSeekComplete(MediaPlayer mp) {
////                        mediaPlayer.start();
////                    }
////                });
//                mediaPlayer.start();
//
//                // mediaPlayer.start();
//                IS_PLAYING=true;
//            }
//        });
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


    private void buildNotification() {

//        MediaSessionCompat mediaSession = new MediaSessionCompat()
//        MediaControllerCompat mediaController = mediaSession.getController();
//        MediaMetadataCompat mediaMetadata = mediaController.getMetadata();
//        MediaDescriptionCompat mediaDescription = mediaMetadata.getDescription();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Music",IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);

        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                //.setShowActionsInCompactView(0))
               // .setSmallIcon(R.drawable.guitarbg)
                .setContentTitle(musicList.get(position).getAlbumName())
                .setContentText(musicList.get(position).getTitle())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.guitarbg)
                .setAutoCancel(false)
                //.addAction(R.drawable.play,)

                .build();

        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());

    }







}
