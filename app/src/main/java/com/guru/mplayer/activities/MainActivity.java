package com.guru.mplayer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.guru.mplayer.R;
import com.guru.mplayer.adapter.SongListAdapter;
import com.guru.mplayer.data_model.MusicData;
import com.guru.mplayer.helper.MediaDataHelper;
import com.guru.mplayer.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemClickListener,ActivityCompat.OnRequestPermissionsResultCallback {

    MediaDataHelper mediaDataHelper = new MediaDataHelper();
    public static int READ_PERMISSION = 5;
    QueryMusicData queryMusicData = new QueryMusicData();
    String TAG = "music";
    RecyclerView songsRecyclerView;
    private ArrayList<MusicData> songsList = new ArrayList();
    SongListAdapter songListAdapter;
    MusicData music_data = new MusicData();
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        isReadStoragePermissionGranted();
        queryMusicData.execute();

        linearLayout = findViewById(R.id.rootview);
        songsRecyclerView = findViewById(R.id.tracks_recycler_view);
        songsRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        songListAdapter = new SongListAdapter(R.layout.list_item, songsList);
        songsRecyclerView.setAdapter(songListAdapter);
        songListAdapter.setClickListener(this);



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




    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onClick(View View, int Position) {
      //  Toast.makeText(this, "from recycler View" + Position, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("songsList", songsList);
        i.putExtra("position", Position);
        startActivity(i);
    }

    public class QueryMusicData extends AsyncTask<Void, Void, ArrayList<MusicData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<MusicData> tempList) {
            super.onPostExecute(tempList);

            songsList.addAll(tempList);
            songListAdapter.notifyDataSetChanged();
            Log.d(TAG + "OnPost", String.valueOf(songsList.size()));


        }

        @Override
        protected ArrayList<MusicData> doInBackground(Void... voids) {

            ArrayList<MusicData> lArrayList;
            lArrayList = mediaDataHelper.queryMediaMeta(MainActivity.this);


            return lArrayList;


        }

    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is already present");
                return true;
            } else {

                Log.d(TAG, "Permission is Denied");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
                return false;
            }
        } else { //permission for lower API devices,granted from manifest
            Log.d(TAG, "Permission is granted Lower API");
            return true;
        }
    }


}
