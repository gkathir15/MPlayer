package com.guru.mplayer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guru.mplayer.R;
import com.guru.mplayer.data_model.Music_Data;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ArrayList<Music_Data> mMusicList = new ArrayList();
    int mSelectedPosition;
    String TAG="player";

    TextView mTitle,mAlbum;
    ImageView mCd,prev,play,next;
    SeekBar mMusicSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mTitle = findViewById(R.id.track_title);
        mAlbum = findViewById(R.id.album_name);
        mCd = findViewById(R.id.cd);
        prev = findViewById(R.id.prev);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        mMusicSeek = findViewById(R.id.seekbar);
        Intent i = getIntent();
        mMusicList = (ArrayList<Music_Data>) i.getSerializableExtra("songsList");
        mSelectedPosition = i.getIntExtra("position",0);
        Log.d(TAG,"passed value"+mSelectedPosition);
        mTitle.setText(mMusicList.get(mSelectedPosition).getTitle());
        mAlbum.setText(mMusicList.get(mSelectedPosition).getAlbumName());
        spinCD(true);


    }


    public boolean spinCD(boolean isSpin)
    {
        do
        {
            for (int i =10;i<360;i=i+10)
            {
                mCd.setRotation(i);
             if (i == 360)
             {
                 i=5;
             }
            }



        }while(isSpin);

        return true;

    }


}
