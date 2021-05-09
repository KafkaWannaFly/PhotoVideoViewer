package com.hcmus.photovideoviewer.viewmodels;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.constants.VideoPreferences;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaFileServices;

import java.io.IOException;

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

	public void insertVideo(String src, String dest, VideoModel videoModel) throws IOException {
		MediaFileServices.copy(context, src, dest);

		ContentValues values = new ContentValues();
		values.put(MediaStore.Video.Media.DISPLAY_NAME, videoModel.displayName);
		values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
		// Add the date meta data to ensure the image is added at the front of the gallery
		values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
		values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis());

		Uri trashUri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
		MediaFileServices.delete(context, trashUri.toString());
	}
}
