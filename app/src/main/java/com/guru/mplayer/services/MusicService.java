package com.guru.mplayer.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
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
    PendingIntent notificationPendingIntent;
    Intent notifyIntent;
    CommonHelper commonHelper = new CommonHelper();
    Bitmap albumArt;









    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
        position = intent.getIntExtra("position", 0);
        position = intent.getIntExtra("position", 0);
        musicList = (ArrayList<MusicData>) intent.getSerializableExtra("songsList");
        mediaID = musicList.get(position).getId();
        mSongsListSize = musicList.size();
        Log.d(TAG, mediaID);
        Log.d(TAG, String.valueOf(mSongsListSize));
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

        return iBinder;

    }
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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

        updateNotification();




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
//       updateNotification();
    }

    public void seekToDuration(final int duration)
    {
        mediaPlayer.seekTo(duration);
        IS_PLAYING=true;

    }


    public void playNext()
    { int temp = musicList.size();
        if (position == temp -1)
        {
            position = 0;
            Log.d("nxt","true");

        }
        else {
            Log.d("nxt","false");

            ++position;
        }
        Log.d(TAG+"next", String.valueOf(position));


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

        updateNotification();

    }

    public void playPrev()
    {

        Log.d("position", String.valueOf(position));
        Log.d("size of list", String.valueOf(mSongsListSize));

        if (position == 0)
        {
            position = mSongsListSize -1;
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

        updateNotification();

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

        updateNotification();

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        notificationManager.cancelAll();

    }

    public int getElapsedTime() {

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                lTempPos = mediaPlayer.getCurrentPosition();
               // Log.d("currpos", String.valueOf(lTempPos));
                return lTempPos;
            } else {
                return lTempPos;
            }
        }
        return lTempPos;

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
        notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationPendingIntent = PendingIntent.getActivity(this,0,notifyIntent,0);
        notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Music",IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //PendingIntent pendingIntent =
        //NotificationCompat.Action = new NotificationCompat.Action(R.drawable.pause,"Pause",)

//
        startForeground(id,commonHelper.setNotification(CHANNEL_ID,musicList.get(position).getAlbumName(),musicList.get(position).getTitle(),this,notificationPendingIntent,commonHelper.albumArtToBitmap(musicList,position)));
        //notificationManager.notify(id,);

    }

    private void updateNotification()
    {
        //albumArt = BitmapFactory.decodeFile(String.valueOf(Uri.parse("content://media/external/audio/albumart/"+musicList.get(position).getAlbumID())));
        notificationManager.notify(id,commonHelper.setNotification(CHANNEL_ID,
                musicList.get(position).getAlbumName(),
                musicList.get(position).getTitle(),
                this,notificationPendingIntent,commonHelper.albumArtToBitmap(musicList,position)));
    }









}
