package com.guru.mplayer.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guru.mplayer.R;
import com.guru.mplayer.data_model.AlbumData;
import com.guru.mplayer.data_model.Music_Data;
import com.guru.mplayer.services.MusicService;
import com.guru.mplayer.services.MusicService.LocalBinder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ArrayList<Music_Data> mMusicList = new ArrayList();
    ArrayList<AlbumData> mAlbumList= new ArrayList<>();
    int mSelectedPosition;
    String TAG = "player";
    int NOTIFICATION_ID = 5;
    String CHANNEL_ID = "5";
    TextView mTitle, mAlbum, mElapsed, mDuration;
    ImageView mCd, prev, play, next;
    SeekBar mSeekBar;
    Animation rotation;
    boolean isBound = false;
    MusicService musicService;
    Intent playIntent;
    Handler mHandler;
    int mSongsListSize;
    //Drawable drawablePlay = getDrawable(R.drawable.play);
    private ServiceConnection mserviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mTitle = (TextView) findViewById(R.id.title);
        mHandler = new Handler();
        mAlbum = (TextView) findViewById(R.id.album);
        mCd = findViewById(R.id.cd);
        prev = findViewById(R.id.prev);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        mSeekBar = findViewById(R.id.seekbar);
        mDuration = findViewById(R.id.duration);
        mElapsed = findViewById(R.id.elapsed);
        rotation = AnimationUtils.loadAnimation(this, R.anim.spin);
        Intent i = getIntent();
        mMusicList = (ArrayList<Music_Data>) i.getSerializableExtra("songsList");
        mSelectedPosition = i.getIntExtra("position", 0);
        mSongsListSize = mMusicList.size();

        Log.d(TAG, "passed value" + mSelectedPosition);
//        Log.d(TAG, mMusicList.get(mSelectedPosition).getTitle());
        mTitle.setText(mMusicList.get(mSelectedPosition).getTitle());
        Log.d(TAG, String.valueOf(mMusicList.get(mSelectedPosition).getLength()));
        mDuration.setText(String.valueOf(mMsToSec(mMusicList.get(mSelectedPosition).getLength())));
        mAlbum.setText(mMusicList.get(mSelectedPosition).getAlbumName());
        setAlbumArt(mSelectedPosition);

        //spinCD(true);
        playIntent = new Intent(this, MusicService.class);
        playIntent.putExtra("songsList", mMusicList);
        playIntent.putExtra("position", mSelectedPosition);
        bindService(playIntent, mserviceConnection, Context.BIND_AUTO_CREATE);
        //updateProgress();


        //setMetaDataOnUI();
        Log.d("isplaying out", String.valueOf(MusicService.IS_PLAYING));


//        while (MusicService.IS_PLAYING) {
//            Log.d("isplaying inLoop", String.valueOf(MusicService.IS_PLAYING));
////            Log.d("elapsed time", String.valueOf(musicService.getElapsedTime()));
//            mElapsed.setText(musicService.getElapsedTime());
//        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick pause");
                pauseOnPlay();
                // updateElapsedTime();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
                //updateElapsedTime();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
                // updateElapsedTime();

            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


               mHandler.removeCallbacks(updateElapsedTime);
                seekBar.setProgress(progress);
                if(fromUser)
                seekTo(progress * musicService.getSongDuration() / 100);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//



            }
        });





    }

    public void setMetaDataOnUI() {
        int lPosition = musicService.position;
        mTitle.setText(mMusicList.get(lPosition).getTitle());
        Log.d(TAG, String.valueOf(mMusicList.get(lPosition).getLength()));
        mDuration.setText(String.valueOf(mMsToSec(mMusicList.get(lPosition).getLength())));
        mAlbum.setText(mMusicList.get(lPosition).getAlbumName());

    }


    public boolean spinCD(boolean isSpin) {

        if (isSpin)
            //mCd.startAnimation(rotation);
            mCd.clearAnimation();
        else
            mCd.clearAnimation();

        return true;

    }

    public String mMsToSec(int milliSec) {
        String calculatedDuration;

        int sec = (milliSec / 1000) % 60;
        int mins = (milliSec / 1000) / 60;

        calculatedDuration = mins + ":" + sec;
        Log.d(TAG, calculatedDuration);
        return calculatedDuration;

    }

    public void pauseOnPlay() {
        Log.d(TAG, "pause play method");
        if (musicService.IS_PLAYING) {
            spinCD(false);
            musicService.pause();
            play.setImageDrawable(getDrawable(R.drawable.play));

        } else if (!musicService.IS_PLAYING) {
            spinCD(true);
            play.setImageDrawable(getDrawable(R.drawable.pause));
            musicService.playOnPause();
            updateProgress();
        }


    }

    public void playNext() {
        spinCD(false);
        mSeekBar.setProgress(0);
        musicService.playNext();
        play.setImageDrawable(getDrawable(R.drawable.pause));

        if (mSelectedPosition == --mSongsListSize)
        {
            mSelectedPosition = 0;
        }
        else {
            mSelectedPosition = ++mSelectedPosition;
        }

        setMetaDataOnUI();
        setAlbumArt(mSelectedPosition);
        spinCD(true);

    }

    public void playPrev() {
        spinCD(false);
        mSeekBar.setProgress(0);
        musicService.playPrev();
        play.setImageDrawable(getDrawable(R.drawable.pause));
        if (mSelectedPosition == 0)
        {
            mSelectedPosition = --mSongsListSize;
        }
        else
        {
            mSelectedPosition = --mSelectedPosition;}
        setMetaDataOnUI();
       setAlbumArt(mSelectedPosition);
        spinCD(true);


    }


    public void seekTo(int duration) {
        musicService.seekToDuration(duration);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // musicService.startForeground(NOTIFICATION_ID,setNotification());
       // musicService.onDestroy();
        //finish();

    }


    void updateProgress() {

        mHandler.postDelayed(updateElapsedTime, 1000);


    }

    private Runnable updateElapsedTime = new Runnable() {
        @Override
        public void run() {
            if (MusicService.IS_PLAYING)
                mSeekBar.setMax(100);
            mSeekBar.setProgress(musicService.getElapsedTime()*100/musicService.getSongDuration());
            mElapsed.setText(mMsToSec(musicService.getElapsedTime()));
            Log.d("updation", String.valueOf(musicService.getElapsedTime()*100/musicService.getSongDuration()));
            Log.d("vals"," elap "+String.valueOf(musicService.getElapsedTime()+" dur "+musicService.getSongDuration()));
            mHandler.postDelayed(this, 100);

        }
    };


    public void setAlbumArt(int pos) {

        Picasso.get().load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
                Long.parseLong(mMusicList.get(pos).getAlbumID())))
                .placeholder(R.drawable.ic_vinyl)
                .error(R.mipmap.dummy)
                .into(mCd);


    }


    Notification setNotification()
    {        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "DEFAULT")
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setContentText(mMusicList.get(mSelectedPosition).getTitle())
                .setContentTitle("Jazz Player")
                .setSmallIcon(R.mipmap.guitar_round);

        Intent resultIntent = new Intent(this, PlayerActivity.class);
        PendingIntent resultPendingIntent =PendingIntent.getActivity(this, NOTIFICATION_ID, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(resultPendingIntent);

        return notification.build();


    }





    @Override
    protected void onStop() {
        super.onStop();
        musicService.startForeground(NOTIFICATION_ID, setNotification());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onCompletion(MediaPlayer mp) {
//
//        setMetaDataOnUI();
//        setAlbumArt(musicService.position);
//
//
//
//    }
}
