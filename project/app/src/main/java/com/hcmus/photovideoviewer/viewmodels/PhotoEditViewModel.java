package com.hcmus.photovideoviewer.viewmodels;

import androidx.lifecycle.MutableLiveData;

public class PhotoEditViewModel {
	MutableLiveData<String> mutableLiveStatus = new MutableLiveData<>("");

	public PhotoEditViewModel() {

	}

	public MutableLiveData<String> getMutableLiveStatus() {
		return mutableLiveStatus;
	}
}
