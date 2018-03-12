package com.guru.mplayer.data_model;

import java.io.Serializable;

/**
 * Created by Guru on 09-03-2018.
 */

public class Music_Data implements Serializable {

    private String TrackURI,Title,AlbumName;
    int Length;

    public Music_Data(String trackURI,
                      String title,
                      int length,
                      String albumName)
    {

        this.Title = title;
        this.TrackURI = trackURI;
        this.Length = length;
        this.AlbumName = albumName;
    }

    public Music_Data() {

    }

    public String getTrackURI() {
        return TrackURI;
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

    public void setTrackURI(String trackURI) {
        TrackURI = trackURI;
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
