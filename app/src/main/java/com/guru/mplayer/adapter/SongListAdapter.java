package com.guru.mplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guru.mplayer.R;
import com.guru.mplayer.data_model.Music_Data;
import com.guru.mplayer.interfaces.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by Guru on 12-03-2018.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private ArrayList<Music_Data> music_Data_List;
    private int itemList;
    private OnItemClickListener clickListener;


    public SongListAdapter(int list_item, ArrayList<Music_Data> songsList) {


        itemList = list_item;
        music_Data_List = songsList;


    }

    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
         View SongsListView = layoutInflater.inflate(itemList,parent,false);

         ViewHolder viewHolder = new ViewHolder(SongsListView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SongListAdapter.ViewHolder holder, int position) {

        Music_Data music_data = music_Data_List.get(position);
        TextView mTitle = holder.title;
        TextView mAlbum = holder.album;
        mTitle.setText("Title: "+music_data.getTitle());
        mAlbum.setText("Album: "+music_data.getAlbumName());
        Log.d("Adapter","Onbinf view holder called");


    }

    @Override
    public int getItemCount() {
        Log.d("adapter", String.valueOf(music_Data_List.size()));
        return music_Data_List.size();

    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,album;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.track_title);
            album = itemView.findViewById(R.id.album_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v,getAdapterPosition());
        }
    }



}
