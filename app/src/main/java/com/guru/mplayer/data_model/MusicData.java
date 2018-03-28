package com.guru.mplayer.data_model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Guru on 09-03-2018.
 */

public class MusicData implements Serializable {

    private String Id,Title,AlbumName,AlbumID;
    int Length;
    String TrackUri;

    public MusicData() {

    }


    public MusicData(String id, String title, int length, String albumName, String albumID) {
        Id = id;
        Title = title;
        AlbumName = albumName;
        Length = length;
        AlbumID = albumID;
    }

    public String getAlbumID() {
        return AlbumID;
    }

    public void setAlbumID(String albumID) {
        AlbumID = albumID;
    }

    public String getTrackUri() {
        return TrackUri;
    }

    public void setTrackUri(String trackUri) {
        TrackUri = trackUri;
    }

//    public MusicData(String string, String mCursorString, int anInt, String albumArt, String s) {
//
//    }

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
