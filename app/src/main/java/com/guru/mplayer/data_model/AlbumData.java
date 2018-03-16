package com.guru.mplayer.data_model;

import java.io.Serializable;

/**
 * Created by Guru on 15-03-2018.
 */

public class AlbumData implements Serializable {

    private String albumNAme,AlbumArt;
    private  String AlbumID;

    public AlbumData(String albumNAme,String  albumID, String albumArt) {
        this.albumNAme = albumNAme;
        AlbumArt = albumArt;
        AlbumID = albumID;
    }

    public AlbumData() {

    }

    public String getAlbumNAme() {
        return albumNAme;
    }

    public void setAlbumNAme(String albumNAme) {
        this.albumNAme = albumNAme;
    }

    public String getAlbumArt() {
        return AlbumArt;
    }

    public void setAlbumArt(String albumArt) {
        AlbumArt = albumArt;
    }

    public String getAlbumID() {
        return AlbumID;
    }

    public void setAlbumID(String albumID) {
        AlbumID = albumID;
    }
}
