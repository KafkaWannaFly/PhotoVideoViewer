package com.hcmus.photovideoviewer.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.hcmus.photovideoviewer.models.VideoModel;

public class VideoViewViewModel {
	private final MutableLiveData<VideoModel> liveVideoModel = new MutableLiveData<>();

	private Context context;

	public VideoViewViewModel(Context context, VideoModel videoModel) {
		this.liveVideoModel.setValue(videoModel);

		this.context = context;
	}

	public MutableLiveData<VideoModel> getLiveVideoModel() {
		return liveVideoModel;
	}
}
