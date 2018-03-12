package com.guru.mplayer.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.guru.mplayer.R;
import com.guru.mplayer.adapter.SongListAdapter;
import com.guru.mplayer.data_model.Music_Data;
import com.guru.mplayer.helper.MediaDataHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MediaDataHelper mediaDataHelper = new MediaDataHelper();
    public static int READ_PERMISSION = 5;
    QueryMusicData queryMusicData = new QueryMusicData();
    String TAG = "music";
    RecyclerView songsRecyclerView;
    private ArrayList<Music_Data> songsList = new ArrayList();
    SongListAdapter songListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isReadStoragePermissionGranted()) {
            queryMusicData.execute();
        } else {
            Toast.makeText(this, "Cannot proceed without permissions", Toast.LENGTH_LONG).show();
        }

         songListAdapter = new SongListAdapter(R.layout.list_item,songsList);
        songsRecyclerView = findViewById(R.id.tracks_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsRecyclerView.setAdapter(songListAdapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queryMusicData.cancel(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        isReadStoragePermissionGranted();


    }

    public class QueryMusicData extends AsyncTask<Void, Void, ArrayList<Music_Data>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<Music_Data> tempList) {
            super.onPostExecute(tempList);

            songsList.addAll(tempList);
            songListAdapter.notifyDataSetChanged();
            Log.d(TAG+"OnPost", String.valueOf(songsList.size()));


        }

        @Override
        protected ArrayList<Music_Data> doInBackground(Void... voids) {

            ArrayList<Music_Data> lArrayList;
           lArrayList =  mediaDataHelper.queryMediaMeta(getApplicationContext());


            return lArrayList;


        }

    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is granted");
                return true;
            } else {

                Log.d(TAG, "Permission is Denied");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
                return false;
            }
        } else { //permission for lower API devices,granted from manifest
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }

}
