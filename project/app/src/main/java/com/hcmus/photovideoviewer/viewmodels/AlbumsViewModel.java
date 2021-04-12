package com.hcmus.photovideoviewer.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

import java.util.ArrayList;

public class AlbumsViewModel extends ViewModel {
    MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance();
    public ArrayList<AlbumModel> getAlbumModels() {
        Log.d("abc", mediaDataRepository + "");
        ArrayList<AlbumModel> albumModels = mediaDataRepository.getAlbumModels();
        return albumModels;
    }
}