package com.hcmus.photovideoviewer.viewmodels;

import androidx.lifecycle.MutableLiveData;

public class PhotoEditViewModel {
	MutableLiveData<String> mutableLiveStatus = new MutableLiveData<>("");
	MutableLiveData<Boolean> isEditToolsVisible = new MutableLiveData<>(true);

	public PhotoEditViewModel() {

	}

	public MutableLiveData<String> getMutableLiveStatus() {
		return mutableLiveStatus;
	}

	public MutableLiveData<Boolean> getIsEditToolsVisible() {
		return isEditToolsVisible;
	}
}
