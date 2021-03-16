package com.hcmus.photovideoviewer.models;

import android.provider.ContactsContract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumModel {
    protected PhotoModel imageUrl;
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

    public PhotoModel getImageUrl() {
        return imageUrl;
    }
}
