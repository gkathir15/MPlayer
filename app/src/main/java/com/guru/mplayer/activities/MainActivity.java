package com.guru.mplayer.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.guru.mplayer.R;
import com.guru.mplayer.helper.MediaDataHelper;

public class MainActivity extends AppCompatActivity {

    MediaDataHelper mediaDataHelper = new MediaDataHelper();
    //public static int READ_PERMISSION = 5;
    boolean isPermissionGranted = false;
    QueryMusicData queryMusicData = new QueryMusicData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryMusicData.execute();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queryMusicData.cancel(true);

    }

    public class QueryMusicData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {

            mediaDataHelper.queryMediaMeta(getApplicationContext());
            return null;
        }
    }

}
