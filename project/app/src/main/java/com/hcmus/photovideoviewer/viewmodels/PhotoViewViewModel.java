package com.hcmus.photovideoviewer.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.hcmus.photovideoviewer.models.PhotoModel;

public class PhotoViewViewModel {
	private final MutableLiveData<PhotoModel> livePhotoModel = new MutableLiveData<>();

	public PhotoViewViewModel(Context context, PhotoModel photoModel) {
		livePhotoModel.setValue(photoModel);
	}

	public MutableLiveData<PhotoModel> getLivePhotoModel() {
		return livePhotoModel;
	}
}
