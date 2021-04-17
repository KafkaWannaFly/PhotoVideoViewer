package com.hcmus.photovideoviewer.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.views.PhotosFragment;

public class PhotoViewViewModel {
	private final MutableLiveData<PhotoModel> livePhotoModel = new MutableLiveData<>();
	private Context context = null;

	public PhotoViewViewModel(Context context, PhotoModel photoModel) {
		this.context = context;
		livePhotoModel.setValue(photoModel);
	}

	public MutableLiveData<PhotoModel> getLivePhotoModel() {
		return livePhotoModel;
	}

	public void setFavorite(boolean isFavorite) {
		PhotoModel photoModel = livePhotoModel.getValue();
		if (photoModel != null) {
			photoModel.isFavorite = isFavorite;

			livePhotoModel.setValue(photoModel);

			saveIsFavorite(photoModel, isFavorite);
		}
	}

	private void saveIsFavorite(PhotoModel photoModel, boolean isFavorite) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("Photos", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(photoModel.uri + "isFavorite", isFavorite);
		editor.apply();

		Log.e("PhotoViewTextClick", "Save isFavorite! " + photoModel.displayName + ", value: " + isFavorite);
	}
}
