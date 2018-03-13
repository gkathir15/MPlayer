package com.guru.mplayer.data_model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Guru on 09-03-2018.
 */

public class Music_Data implements Serializable {

    private String Id,Title,AlbumName;
    int Length;
    String TrackUri;

    public Music_Data(String id,
                      String title,
                      int length,
                      String albumName)
    {

        this.Title = title;
        this.Id = id;
        this.Length = length;
        this.AlbumName = albumName;
    }

    public String getTrackUri() {
        return TrackUri;
    }

    public void setTrackUri(String trackUri) {
        TrackUri = trackUri;
    }

    public Music_Data() {

    }

    public String getId() {
        return Id;
    }

    public int getLength() {
        return Length;
    }

    public String getTitle() {
        return Title;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setLength(int length) {
        Length = length;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

}
