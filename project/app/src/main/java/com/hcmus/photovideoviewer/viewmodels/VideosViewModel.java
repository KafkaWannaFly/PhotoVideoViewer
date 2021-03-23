package com.hcmus.photovideoviewer.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

import java.util.ArrayList;

public class VideosViewModel extends ViewModel {
	private final MutableLiveData<ArrayList<VideoModel>> liveVideoModels;

	public VideosViewModel(ArrayList<VideoModel> videoModels) {
		liveVideoModels = new MutableLiveData<>(videoModels);
	}

	public MutableLiveData<ArrayList<VideoModel>> getLiveVideoModels() {
		return liveVideoModels;
	}

}