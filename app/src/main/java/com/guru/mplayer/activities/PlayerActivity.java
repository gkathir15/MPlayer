package com.guru.mplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
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
import com.guru.mplayer.data_model.Music_Data;
import com.guru.mplayer.services.MusicService;
import com.guru.mplayer.services.MusicService.LocalBinder;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ArrayList<Music_Data> mMusicList = new ArrayList();
    int mSelectedPosition;
    String TAG="player";

    TextView mTitle,mAlbum,mElapsed,mDuration;
    ImageView mCd,prev,play,next;
    SeekBar mMusicSeek;
    Animation rotation;
    boolean isBound = false;
    MusicService musicService;
    Intent playIntent;
    //Drawable drawablePlay = getDrawable(R.drawable.play);
    private ServiceConnection mserviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder)service;
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
        mSelectedPosition = i.getIntExtra("position",0);
        Log.d(TAG,"passed value"+mSelectedPosition);
        Log.d(TAG,mMusicList.get(mSelectedPosition).getTitle());
        mTitle.setText(mMusicList.get(mSelectedPosition).getTitle());
        Log.d(TAG, String.valueOf(mMusicList.get(mSelectedPosition).getLength()));
        mDuration.setText(String.valueOf(mMsToSec(mMusicList.get(mSelectedPosition).getLength())));
        mAlbum.setText(mMusicList.get(mSelectedPosition).getAlbumName());
        spinCD(true);
         playIntent = new Intent(this, MusicService.class);
        playIntent.putExtra("songsList",mMusicList);
        playIntent.putExtra("position",mSelectedPosition);
        bindService(playIntent,mserviceConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onclick pause");
                pauseplay();
            }
        });

//        prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playPrev();
//            }
//        });
//
//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playNext();
//            }
//        });



    }




    public boolean spinCD(boolean isSpin)
    {

        if(isSpin)
            mCd.startAnimation(rotation);
        else
            mCd.clearAnimation();

        return true;

    }

    public String mMsToSec(int milliSec)
    {
        String calculatedDuration;

        int sec = (milliSec/1000)%60;
        int mins = (milliSec/1000)/60;

        calculatedDuration = mins+":"+sec;
        Log.d(TAG,calculatedDuration);
        return calculatedDuration;

    }

    public void pauseplay()
    {
        Log.d(TAG,"pause play method");
       if(musicService.IS_PLAYING )
        {
            musicService.pause();
         play.setImageDrawable(getDrawable(R.drawable.play));
         spinCD(false);
        }
        else
            if (!musicService.IS_PLAYING){
           play.setImageDrawable(getDrawable(R.drawable.pause));
            musicService.playOnPause();
            spinCD(true);}


    }

    public void playNext()
    {

    }

    public void playPrev()
    {

    }

    public void seekTo()
    {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        musicService.onDestroy();
        finish();
        musicService.onUnbind(playIntent);
    }

}
