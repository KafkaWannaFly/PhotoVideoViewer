package com.hcmus.photovideoviewer.viewmodels;

import androidx.lifecycle.ViewModel;

import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

import java.util.ArrayList;

public class PhotosViewModel extends ViewModel {
	MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance();

	public ArrayList<PhotoModel> getPhotoModels() {
		return mediaDataRepository.getPhotoModels();
	}
}