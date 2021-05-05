package com.hcmus.photovideoviewer.models;

import android.net.Uri;
import android.provider.ContactsContract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumModel {
    protected long imageUrl;
    protected String albumName;
    protected int quantity;
    @NotNull
    @Override
    public String toString()
    {
        return "AlbumModel{" +
                "albumName=" + albumName +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity='" + quantity + '\'' +
               '}';
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getImageUrl() {
        return imageUrl;
    }
}
