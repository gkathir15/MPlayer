package com.guru.mplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guru.mplayer.R;
import com.guru.mplayer.data_model.MusicData;
import com.guru.mplayer.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Guru on 12-03-2018.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private ArrayList<MusicData> musicDataList;
    private int itemList;
    private OnItemClickListener clickListener;
    Context mContext;


    public SongListAdapter(int list_item, ArrayList<MusicData> songsList) {


        itemList = list_item;
        musicDataList = songsList;


    }

    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View SongsListView = layoutInflater.inflate(itemList, parent, false);

        ViewHolder viewHolder = new ViewHolder(SongsListView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SongListAdapter.ViewHolder holder, int position) {


        MusicData music_data = musicDataList.get(position);
        TextView mTitle = holder.title;
        TextView mAlbum = holder.album;
        ImageView lThumbArt = holder.lThumbsArt;
        mTitle.setText(music_data.getTitle());
        mAlbum.setText(music_data.getAlbumName());
//        Uri uri =ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
//                Long.parseLong(musicDataList.get(position).getAlbumID()));
       // Uri uri = Uri.parse("content://media/external/audio/albumart"+musicDataList.get(position).getAlbumID());

        Log.d("Via uriParse","content://media/external/audio/albumart/"+ musicDataList.get(position).getAlbumID());
        Log.d("Via WithAppendedID",
                String.valueOf(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
                        Long.parseLong(musicDataList.get(position).getAlbumID()))));

        Log.d("albumID",""+ musicDataList.get(position).getAlbumID());
        //Picasso.get().load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),Long.parseLong(musicDataList.get(position).getAlbumID())))
        Picasso.get().load(Uri.parse("content://media/external/audio/albumart/"+ musicDataList.get(position).getAlbumID()))
        .placeholder(R.drawable.ic_vinyl)
                .error(R.drawable.ic_vinyl)
                .into(lThumbArt);






        Log.d("onCreateView", "On bind view holder called" + position);


    }

    @Override
    public int getItemCount() {
        // Log.d("adapter", String.valueOf(musicDataList.size()));
        return musicDataList.size();

    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, album;
        ImageView lThumbsArt;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.track_title);
            album = itemView.findViewById(R.id.album_name);
            lThumbsArt = itemView.findViewById(R.id.thumbs);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            clickListener.onClick(v, getAdapterPosition());
        }
    }


}
