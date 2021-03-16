package com.hcmus.photovideoviewer.viewmodels;

import androidx.lifecycle.ViewModel;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.mediaHolders.pictureFolderContent;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

import java.util.ArrayList;

public class AlbumsViewModel extends ViewModel {
    MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance();
    public ArrayList<AlbumModel> getAlbumModels() {
        return mediaDataRepository.getAlbumModels();
    }
}