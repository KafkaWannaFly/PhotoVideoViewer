package com.hcmus.photovideoviewer.models;

public class Albums {
    private String imageUrl;
    private String albumName;
    public Albums(String imageUrl, String albumName)
    {
        this.albumName = albumName;
        this.imageUrl = imageUrl;
    }
    public String getImageUrl()
    {
        return this.imageUrl;
    }
    public String getAlbumName()
    {
        return this.albumName;
    }
}
