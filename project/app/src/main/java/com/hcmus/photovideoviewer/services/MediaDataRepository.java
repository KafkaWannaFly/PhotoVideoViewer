package com.hcmus.photovideoviewer.services;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hcmus.photovideoviewer.MainApplication;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;

import java.util.ArrayList;
import java.util.Date;

public class MediaDataRepository {
	@SuppressLint("StaticFieldLeak")
	private static MediaDataRepository instance = null;
	private final Context context;
	private ArrayList<PhotoModel> photoModels = new ArrayList<>();
	private ArrayList<VideoModel> videoModels = new ArrayList<>();

	private MediaDataRepository() {
		context = MainApplication.getContext();
		this.fetchPhotos();
		this.fetchVideos();
	}

	public static MediaDataRepository getInstance() {
		if (instance == null) {
			instance = new MediaDataRepository();
		}
		return instance;
	}

	public ArrayList<PhotoModel> getPhotoModels() {
		return photoModels;
	}

	public ArrayList<VideoModel> getVideoModels() {
		return videoModels;
	}

	private void fetchPhotos() {
		Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_MODIFIED,
				MediaStore.Images.Media.SIZE
		};

		ContentResolver contentResolver = context.getContentResolver();
		try (Cursor cursor = contentResolver.query(_uri,
				projection,
				null,
				null,
				MediaStore.Images.Media.DATE_MODIFIED + " DESC")) {
			int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
			int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
			int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);

			while (cursor.moveToNext()) {
				long _id = cursor.getLong(idColumn);
				String name = cursor.getString(nameColumn);
				Long _size = cursor.getLong(sizeColumn);
				Long date = cursor.getLong(dateColumn);

				PhotoModel photoModel = new PhotoModel() {
					{
						id = _id;
						displayName = name;
						size = _size;
						dateModified = new Date(date * 1000);
						uri = ContentUris.withAppendedId(_uri, id);
					}
				};

				photoModels.add(photoModel);
			}
		}

		Log.d("Images", "Found " + photoModels.size() + " photos");
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	private void fetchVideos() {
		Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		String[] projection = new String[]{
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DATE_MODIFIED,
				MediaStore.Video.Media.SIZE,
				MediaStore.Video.Media.DURATION
		};

		ContentResolver contentResolver = context.getContentResolver();
		try (Cursor cursor = contentResolver.query(uri,
				projection,
				null,
				null,
				MediaStore.Video.Media.DATE_MODIFIED + " DESC")) {
			int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
			int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
			int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
			int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
			int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

			while (cursor.moveToNext()) {
				long _id = cursor.getLong(idColumn);
				String name = cursor.getString(nameColumn);
				Long _size = cursor.getLong(sizeColumn);
				Long date = cursor.getLong(dateColumn);
				Long _duration = cursor.getLong(durationColumn);

				VideoModel videoModel = new VideoModel() {
					{
						id = _id;
						displayName = name;
						size = _size;
						dateModified = new Date(date * 1000);
						duration = _duration/1000;
					}
				};

				videoModels.add(videoModel);
			}
		}

		Log.d("Videos", "Found " + videoModels.size() + " Videos");
	}
}
