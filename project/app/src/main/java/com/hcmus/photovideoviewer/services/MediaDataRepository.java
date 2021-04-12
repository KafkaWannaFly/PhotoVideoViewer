package com.hcmus.photovideoviewer.services;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.VideoGet;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.CodeBoy.MediaFacer.mediaHolders.pictureFolderContent;
import com.CodeBoy.MediaFacer.mediaHolders.videoContent;
import com.hcmus.photovideoviewer.MainApplication;
import com.hcmus.photovideoviewer.models.AlbumModel;
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
	private ArrayList<AlbumModel> albumModels = new ArrayList<>();

	private MediaDataRepository() {
		context = MainApplication.getContext();
	}

	public static MediaDataRepository getInstance() {
		if (instance == null) {
			instance = new MediaDataRepository();
		}
		return instance;
	}

	public void fetchData() {
		try {
			this.fetchPhotos();
			this.fetchVideos();
			this.fetchAlbums();
		} catch (Exception exception) {
			Log.d("Exception", exception.getMessage());
		}

	}

	public ArrayList<PhotoModel> getPhotoModels() {
		return photoModels;
	}

	public ArrayList<VideoModel> getVideoModels() {
		return videoModels;
	}

	public ArrayList<AlbumModel> getAlbumModels() {
		return albumModels;
	}

	public ArrayList<PhotoModel> fetchPhotos() {
		photoModels.clear();

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
		return this.photoModels;
	}

	public ArrayList<VideoModel> fetchVideos() {
		videoModels.clear();

		Uri _uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		String[] projection = new String[]{
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DATE_MODIFIED,
				MediaStore.Video.Media.SIZE,
				MediaStore.Video.Media.DURATION
		};

		ContentResolver contentResolver = context.getContentResolver();
		try (Cursor cursor = contentResolver.query(_uri,
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
						duration = _duration / 1000;
						uri = ContentUris.withAppendedId(_uri, id);
					}
				};

				videoModels.add(videoModel);
			}
		}

		Log.d("Videos", "Found " + videoModels.size() + " Videos");

		return this.videoModels;
	}

	public ArrayList<AlbumModel> fetchAlbums() {
		albumModels.clear();
		Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ArrayList<pictureContent> allPhotosAlbum;
		ArrayList<videoContent> allVideosAlbum;

		ArrayList<pictureFolderContent> pictureFolders = new ArrayList<>();
		pictureFolders.addAll(MediaFacer.withPictureContex(context).getPictureFolders());
		for (int i = 0; i < pictureFolders.size(); i++) {
			int index = i;
			allPhotosAlbum = MediaFacer.withPictureContex(context).getAllPictureContentByBucket_id(pictureFolders.get(index).getBucket_id());
			allVideosAlbum = MediaFacer.withVideoContex(context).getAllVideoContentByBucket_id(pictureFolders.get(index).getBucket_id());
			ArrayList<pictureContent> finalAllPhotos = allPhotosAlbum;
			ArrayList<videoContent> finalAllVideos = allVideosAlbum;

			PhotoModel photoModel = new PhotoModel() {
				{
					id = (long) finalAllPhotos.get(finalAllPhotos.size() - 1).getPictureId();
					displayName = finalAllPhotos.get(finalAllPhotos.size() - 1).getPicturName();
					size = finalAllPhotos.get(finalAllPhotos.size() - 1).getPictureSize();
					dateModified = new Date(finalAllPhotos.get(finalAllPhotos.size() - 1).getDate_modified() * 1000);
					uri = ContentUris.withAppendedId(_uri, id);
				}
			};
			AlbumModel albumModel = new AlbumModel() {
				{
					albumName = pictureFolders.get(index).getFolderName();
					quantity = finalAllPhotos.size() + finalAllVideos.size();
					imageUrl = photoModel;
				}
			};
			albumModels.add(albumModel);
		}
		Log.d("Size of Album: ", "" + albumModels.get(0));
		return albumModels;
	}
}
