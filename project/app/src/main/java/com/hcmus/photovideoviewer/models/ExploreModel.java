package com.hcmus.photovideoviewer.models;

import android.graphics.Bitmap;

import org.jetbrains.annotations.NotNull;

public class ExploreModel {
//    @NotNull
//    @Override
//    public String toString()
//    {
//        return "ExploreModel{" +
//                "avatarUrl=" + avatarUrl +
//                '}';
//    }
    protected int idPerson;
    protected Bitmap bitmapAvatar;
    public Bitmap getBitmapAvatar(){return bitmapAvatar;}
    public int getIdPerson(){
        return idPerson;
    }
}
