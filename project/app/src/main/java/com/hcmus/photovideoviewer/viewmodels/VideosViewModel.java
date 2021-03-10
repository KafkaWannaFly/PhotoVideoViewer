package com.hcmus.photovideoviewer.viewmodels;

import androidx.lifecycle.ViewModel;

import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

import java.util.ArrayList;

public class VideosViewModel extends ViewModel {
	MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance();

	public ArrayList<VideoModel> getVideoModels() {
		return mediaDataRepository.getVideoModels();
	}
}