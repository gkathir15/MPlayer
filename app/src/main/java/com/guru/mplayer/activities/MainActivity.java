package com.guru.mplayer.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.time.Duration;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemClickListener,ActivityCompat.OnRequestPermissionsResultCallback {

    MediaDataHelper mediaDataHelper = new MediaDataHelper();
    public static int READ_PERMISSION_CODE = 5;
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
        linearLayout = findViewById(R.id.rootview);


//        isReadStoragePermissionGranted();
//        queryMusicData.execute();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG,"Permission not present");
            isReadStoragePermissionGranted();

        }
        else {
            queryMusicData.execute();
        }



        songsRecyclerView = findViewById(R.id.tracks_recycler_view);
        songsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        songListAdapter = new SongListAdapter(R.layout.list_item, songsList);
        songsRecyclerView.setAdapter(songListAdapter);
        songListAdapter.setClickListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queryMusicData.cancel(true);


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


    public void isReadStoragePermissionGranted() {

        String []perms ={Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {




            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,perms,READ_PERMISSION_CODE);



              // showSnackBar();



            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);


                
            }
        } else
            {
            // Permission has already been granted
            queryMusicData.execute();
        }

        //        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Permission is already present");
//                return true;
//            } else {
//
//                Log.d(TAG, "Permission is Denied");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
//                return false;
//            }
//        } else {
//            Log.d(TAG, "Permission is granted Lower API");
//            return true;
//        }
//    }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       // isReadStoragePermissionGranted();
        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
           // showSnackBar();
            Log.d(TAG,"on Request permission");
            queryMusicData.execute();

        }
        else
        {

           // queryMusicData.execute();
            showSnackBar();
        }



    }


    void showSnackBar()
    {
        Snackbar snackbar = Snackbar.make(linearLayout, "Enable permissions in App settings", BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.setAction("Settings", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.parse("package:"+ getPackageName());
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        snackbar.show();
    }
}
