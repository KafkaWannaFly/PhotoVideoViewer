package com.hcmus.photovideoviewer.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.constants.VideoPreferences;
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

	public void setFavorite(boolean isFavorite) {
		VideoModel videoModel = liveVideoModel.getValue();
		assert videoModel != null;

		videoModel.isFavorite = isFavorite;
		liveVideoModel.setValue(videoModel);

		this.saveVideoFavoritePreference(videoModel);
	}

	private void saveVideoFavoritePreference(VideoModel videoModel) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(VideoPreferences.VIDEOS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(VideoPreferences.favoritePreferenceOf(videoModel), videoModel.isFavorite);
		editor.apply();
	}
}
