package com.hcmus.photovideoviewer.models;

import java.util.ArrayList;
import java.util.List;

public class AlbumList {
    private static List<Albums> albumsList;
    public static List<Albums> setAlbumsData(){
        albumsList = new ArrayList<>();
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg",
                "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));
        albumsList.add(new Albums("https://image.tmdb.org/t/p/original/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg", "Movie Title"));

        return albumsList;
    }
}
