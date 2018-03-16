package com.guru.mplayer.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
    SeekBar mMusicSeek;
    Animation rotation;
    boolean isBound = false;
    MusicService musicService;
    Intent playIntent;
    Handler mHandler;
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
        mMusicSeek = findViewById(R.id.seekbar);
        mDuration = findViewById(R.id.duration);
        mElapsed = findViewById(R.id.elapsed);
        rotation = AnimationUtils.loadAnimation(this, R.anim.spin);
        Intent i = getIntent();
        mMusicList = (ArrayList<Music_Data>) i.getSerializableExtra("songsList");
        mSelectedPosition = i.getIntExtra("position", 0);
        mAlbumList = (ArrayList<AlbumData>) i.getSerializableExtra("albumList");

        Log.d(TAG, "passed value" + mSelectedPosition);
        Log.d(TAG, mMusicList.get(mSelectedPosition).getTitle());
        mTitle.setText(mMusicList.get(mSelectedPosition).getTitle());
        Log.d(TAG, String.valueOf(mMusicList.get(mSelectedPosition).getLength()));
        mDuration.setText(String.valueOf(mMsToSec(mMusicList.get(mSelectedPosition).getLength())));
        mAlbum.setText(mMusicList.get(mSelectedPosition).getAlbumName());
        setAlbumArt(mSelectedPosition);

        //spinCD(true);
        playIntent = new Intent(this, MusicService.class);
        playIntent.putExtra("songsList", mMusicList);
        playIntent.putExtra("position", mSelectedPosition);
        startService(playIntent);
        bindService(playIntent, mserviceConnection, Context.BIND_AUTO_CREATE);
        // updateProgress();


        //setMetaDataOnUI();
        Log.d("isplaying out", String.valueOf(MusicService.IS_PLAYING));


        while (MusicService.IS_PLAYING) {
            Log.d("isplaying inLoop", String.valueOf(MusicService.IS_PLAYING));
            Log.d("elapsed time", String.valueOf(musicService.getElapsedTime()));
            mElapsed.setText(musicService.getElapsedTime());
        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick pause");
                pauseplay();
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


        mMusicSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


//                mHandler.removeCallbacks(updateElapsedTime);
                seekBar.setProgress(progress);
                seekTo(progress * musicService.getSongDuration() / 100);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                while(musicService.IS_PLAYING)
//                {
//                    mMusicSeek.setProgress((musicService.getElapsedTime()/musicService.getSongDuration())/100);
//                }

//                updateProgress();

            }
        });



    }

    public void setMetaDataOnUI() {
        int lPosition = musicService.position;
        mTitle.setText(mMusicList.get(lPosition).getTitle());
        Log.d(TAG, String.valueOf(mMusicList.get(lPosition).getLength()));
        mDuration.setText(String.valueOf(mMsToSec(mMusicList.get(lPosition).getLength())));
        mAlbum.setText(mMusicList.get(lPosition).getAlbumName());
//        while(MusicService.IS_PLAYING)
//        {
//            Log.d("elapsed time", String.valueOf(musicService.getElapsedTime()));
//            mElapsed.setText(musicService.getElapsedTime());
//        }

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

    public void pauseplay() {
        Log.d(TAG, "pause play method");
        if (musicService.IS_PLAYING) {
            spinCD(false);
            musicService.pause();
            play.setImageDrawable(getDrawable(R.drawable.play));

        } else if (!musicService.IS_PLAYING) {
            spinCD(true);
            play.setImageDrawable(getDrawable(R.drawable.pause));
            musicService.playOnPause();
        }


    }

    public void playNext() {
        spinCD(false);
        mMusicSeek.setProgress(0);
        musicService.playNext();
        play.setImageDrawable(getDrawable(R.drawable.pause));
        setMetaDataOnUI();
        setAlbumArt(mSelectedPosition+1);
        spinCD(true);

    }

    public void playPrev() {
        spinCD(false);
        mMusicSeek.setProgress(0);
        musicService.playPrev();
        play.setImageDrawable(getDrawable(R.drawable.pause));
        setMetaDataOnUI();
        setAlbumArt(mSelectedPosition-1);
        spinCD(true);


    }


    public void seekTo(int duration) {
        musicService.seekToDuration(duration);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        musicService.onDestroy();
        finish();
        musicService.onUnbind(playIntent);
    }


//    void updateProgress() {
//        mHandler.postDelayed(updateElapsedTime, 1000);
//
//    }

//    private Runnable updateElapsedTime = new Runnable() {
//        @Override
//        public void run() {
//            mMusicSeek.setProgress(((musicService.getElapsedTime() / musicService.getSongDuration()) * 100));
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            mHandler.postDelayed(this, 100);
//
//        }
//    };


    public void setAlbumArt(int pos) {
        for (AlbumData temp : mAlbumList) {
            if (mMusicList.get(pos).getAlbumName().equals(temp.getAlbumNAme())) {

                Log.d("albums", temp.getAlbumNAme());

                Picasso.get().load("file://" + temp.getAlbumArt())
                        .placeholder(R.drawable.ic_vinyl)
                        .error(R.drawable.ic_vinyl).
                        into(mCd);
                Log.d("art","Album art found");


            } else {

                Picasso.get().load(R.drawable.ic_vinyl).into(mCd);
                Log.d("art","Album art not found");

            }

        }


    }

    Notification setNotification()
    {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentText(mMusicList.get(mSelectedPosition).getTitle())
                .setContentTitle("Jazz Player")
                .setSmallIcon(R.drawable.ic_acoustic_guitar);

        Intent resultIntent = new Intent(this, PlayerActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
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
}
