package com.guru.mplayer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guru.mplayer.R;
import com.guru.mplayer.adapter.SongListAdapter;
import com.guru.mplayer.data_model.Music_Data;
import com.guru.mplayer.helper.MediaDataHelper;
import com.guru.mplayer.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemClickListener,ActivityCompat.OnRequestPermissionsResultCallback {

    MediaDataHelper mediaDataHelper = new MediaDataHelper();
    public static int READ_PERMISSION = 5;
    QueryMusicData queryMusicData = new QueryMusicData();
    String TAG = "music";
    RecyclerView songsRecyclerView;
    private ArrayList<Music_Data> songsList = new ArrayList();
    SongListAdapter songListAdapter;
    Music_Data music_data = new Music_Data();
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        isReadStoragePermissionGranted();
//        queryMusicData.execute();
        if (isReadStoragePermissionGranted()) {
            queryMusicData.execute();
        } else {
            Toast.makeText(this, "Cannot proceed without permissions", Toast.LENGTH_LONG).show();
        }

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
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        String TAG = "LOG_PERMISSION";
        Log.d(TAG, "Permission callback called-------");


                    if (requestCode == (PackageManager.PERMISSION_GRANTED))
                    {
                        Log.d(TAG, "Phone state and storage permissions granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        queryMusicData.execute();
                    } else {
                        Log.d(TAG, "permission not granted ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                        {
                            showDialogOK("storage permissions required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    isReadStoragePermissionGranted();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            Snackbar.make(linearLayout,"Goto Settings",Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);

                                }
                            }).show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
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
            Log.d(TAG + "OnPost", String.valueOf(songsList.size()));


        }

        @Override
        protected ArrayList<Music_Data> doInBackground(Void... voids) {

            ArrayList<Music_Data> lArrayList;
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
