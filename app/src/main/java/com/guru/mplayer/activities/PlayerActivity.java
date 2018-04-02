package com.guru.mplayer.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
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
import com.guru.mplayer.data_model.MusicData;
import com.guru.mplayer.services.MusicService;
import com.guru.mplayer.services.MusicService.LocalBinder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ArrayList<MusicData> mMusicList = new ArrayList();
    int mSelectedPosition;
    static String TAG = "player";
    int notificationID = 5;
    String channelID = "5";
    TextView mTitle, mAlbum, mElapsed, mDuration;
    ImageView mCd, mPrev, mPlay, mNext;
    SeekBar mSeekBar;
    Animation rotation;
    boolean isBound = false;
    MusicService musicService;
    Intent playerIntent;
    Handler mHandler;
    int mSongsListSize;
    private HeadSetIsPlugged headsetIsPlugged;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            //musicService.playAtPos(mSelectedPosition);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mTitle = findViewById(R.id.title);
        mHandler = new Handler();
        mAlbum = findViewById(R.id.album);
        mCd = findViewById(R.id.cd);
        mPrev = findViewById(R.id.prev);
        mPlay = findViewById(R.id.play);
        mNext = findViewById(R.id.next);
        mSeekBar = findViewById(R.id.seekbar);
        mDuration = findViewById(R.id.duration);
        mElapsed = findViewById(R.id.elapsed);
        rotation = AnimationUtils.loadAnimation(this, R.anim.spin);
        Intent i = getIntent();
        mMusicList = (ArrayList<MusicData>) i.getSerializableExtra("songsList");
        mSelectedPosition = i.getIntExtra("position", 0);
        mSongsListSize = mMusicList.size();
        mSeekBar.setMax(100);

        Log.d(TAG, "passed value" + mSelectedPosition);
//        Log.d(TAG, mMusicList.get(mSelectedPosition).getTitle());
        mTitle.setText(mMusicList.get(mSelectedPosition).getTitle());
        Log.d(TAG, String.valueOf(mMusicList.get(mSelectedPosition).getLength()));
        mDuration.setText(String.valueOf(mMsToSec(mMusicList.get(mSelectedPosition).getLength())));
        mAlbum.setText(mMusicList.get(mSelectedPosition).getAlbumName());
        setAlbumArt(mSelectedPosition);

        //spinCD(true);
        playerIntent = new Intent(getApplicationContext(), MusicService.class);
        playerIntent.putExtra("songsList", mMusicList);
        playerIntent.putExtra("position", mSelectedPosition);
       // startService(playerIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(playerIntent);
        }else
        {
            startService(playerIntent);
        }

        if(!isBound)
        {
            Log.d("PlayerActivity","is not bound,binding Service");

            bindService(playerIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            updateProgress();

        }
        else
            {
                musicService.playAtPos(mSelectedPosition);
                Log.d("playerActivity","Service is bound");

            }

            Log.d(TAG+" position", String.valueOf(mSelectedPosition));
            Log.d(TAG+" size of list", String.valueOf(mMusicList.size()));



        //setMetaDataOnUI();
        Log.d("isplaying out", String.valueOf(MusicService.IS_PLAYING));



        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick pause");
                pauseOnPlay();
            }
        });

        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();

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

         // registering Broadcast Receivers
         headsetIsPlugged= new HeadSetIsPlugged();
        IntentFilter filter = new IntentFilter(MusicService.COMPLETION_CAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(completionReceiver,filter);
        IntentFilter lHeadsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetIsPlugged,lHeadsetFilter);








    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class HeadSetIsPlugged extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra("state",-1)== 0)
            {
                pause();

            }
        }
    }

    private BroadcastReceiver completionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast recieved","updating mNext meta");
            if (intent.getStringExtra("status")== "playing next")
            {

                mHandler.removeCallbacks(updateElapsedTime);
                mSelectedPosition = intent.getIntExtra("position",++mSelectedPosition);
                Log.d("Broadcast recieved", String.valueOf(mSelectedPosition));
                setMetaDataOnUI();
                setAlbumArt(mSelectedPosition);
                updateProgress();

            }


        }
    };

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

        //calculatedDuration = mins + ":" + sec;
//        Log.d(TAG, calculatedDuration);
        String lCalcTime = String.format("%02d:%02d", mins, sec);
       // Log.d(TAG, lCalcTime);
        return lCalcTime;

    }

    public void pauseOnPlay() {
        Log.d(TAG, "pause mPlay method");
        if (musicService.IS_PLAYING) {
            spinCD(false);
            musicService.pause();
            mPlay.setImageDrawable(getDrawable(R.drawable.play));

        } else if (!musicService.IS_PLAYING) {
            spinCD(true);
            mPlay.setImageDrawable(getDrawable(R.drawable.pause));
            musicService.playOnPause();
           updateProgress();
        }


    }

    public  void pause()
    {
        if (musicService.IS_PLAYING) {
            musicService.pause();
            mPlay.setImageDrawable(getDrawable(R.drawable.play));}

    }

    public void playNext() {
        spinCD(false);
        mHandler.removeCallbacks(updateElapsedTime);
        mElapsed.setText("00:00");
        mSeekBar.setProgress(0);
        musicService.playNext();
        mPlay.setImageDrawable(getDrawable(R.drawable.pause));
        int temp = mMusicList.size();

        if (mSelectedPosition == temp-1)
        {
            mSelectedPosition = 0;
        }
        else {
            ++mSelectedPosition;
        }
        Log.d(TAG+"next", String.valueOf(mSelectedPosition));

        setMetaDataOnUI();
        setAlbumArt(mSelectedPosition);
        spinCD(true);
        updateProgress();

    }

    public void playPrev() {
        mHandler.removeCallbacks(updateElapsedTime);
        mElapsed.setText("00:00");
        spinCD(false);
        mSeekBar.setProgress(0);
        musicService.playPrev();
        mPlay.setImageDrawable(getDrawable(R.drawable.pause));
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
        updateProgress();


    }


    public void seekTo(int duration) {
        musicService.seekToDuration(duration);
        updateProgress();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // musicService.startForeground(notificationID,setNotification());
       // musicService.onDestroy();
        //finish();

    }


    void updateProgress() {

        mHandler.postDelayed(updateElapsedTime, 1000);


    }

    private Runnable updateElapsedTime = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(musicService.getElapsedTime()*100/musicService.getSongDuration());
            mElapsed.setText(mMsToSec(musicService.getElapsedTime()));
           // Log.d("updation", String.valueOf(musicService.getElapsedTime()*100/musicService.getSongDuration()));
           // Log.d("vals"," elap "+String.valueOf(musicService.getElapsedTime()+" dur "+musicService.getSongDuration()));
            mHandler.postDelayed(this, 100);
        }
    };


    public void setAlbumArt(int pos) {

        Picasso.get().load(Uri.parse("content://media/external/audio/albumart/"+ mMusicList.get(pos).getAlbumID()))
                .placeholder(R.drawable.ic_vinyl)
                .error(R.mipmap.dummy)
                .into(mCd);


    }





    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(updateElapsedTime);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onStop","onstop called");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(completionReceiver);
        if(headsetIsPlugged != null)
        unregisterReceiver(headsetIsPlugged);
        headsetIsPlugged = null;
        unbindService(mServiceConnection);
        if(!musicService.isIsPlaying())
        {
            stopService(playerIntent);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(playerIntent);
//        }


    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", isBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isBound = savedInstanceState.getBoolean("serviceStatus");
    }
}
