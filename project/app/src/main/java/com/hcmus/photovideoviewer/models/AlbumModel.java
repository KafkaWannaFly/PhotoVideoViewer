package com.hcmus.photovideoviewer.models;

import android.net.Uri;
import android.provider.ContactsContract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumModel {
    protected long imageUrl;
    protected String albumName;
    protected int quantityPhoto;
    protected int quantityVideo;
    @NotNull
    @Override
    public String toString()
    {
        return "AlbumModel{" +
                "albumName=" + albumName +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantityPhoto='" + quantityPhoto + '\'' +
                ", quantityVideo='" + quantityVideo + '\'' +
               '}';
    }

    public int getQuantityPhoto() {
        return quantityPhoto;
    }

    public int getQuantityVideo() {return quantityVideo; }

    public String getAlbumName() {
        return albumName;
    }

    public long getImageUrl() {
        return imageUrl;
    }
}
