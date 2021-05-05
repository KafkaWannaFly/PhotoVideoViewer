package com.hcmus.photovideoviewer.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.ExploreModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

import java.util.ArrayList;

public class ExploreViewModel extends ViewModel {
    MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance();
    public ArrayList<ExploreModel> getExploreModels() {
        Log.d("abc", mediaDataRepository + "");
        ArrayList<ExploreModel> exploreModels = mediaDataRepository.getExploreModels();
        return exploreModels;
    }

}