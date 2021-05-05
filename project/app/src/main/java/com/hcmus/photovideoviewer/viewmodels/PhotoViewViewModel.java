package com.hcmus.photovideoviewer.viewmodels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hcmus.photovideoviewer.constants.FolderConstants;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoViewViewModel {
	private final MutableLiveData<PhotoModel> livePhotoModel = new MutableLiveData<>();
	private Context context = null;
	private Activity activity = null;

	public PhotoViewViewModel(Activity activity, PhotoModel photoModel) {
//		this.context = context;
		this.activity = activity;
		this.context = activity.getApplicationContext();
		livePhotoModel.setValue(photoModel);
	}

	public static String getDataColumn(Context context, Uri uri, String selection,
	                                   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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

	public void setPrivate(boolean isPrivate) throws IOException {
		PhotoModel photoModel = livePhotoModel.getValue();

		if (photoModel != null) {
			photoModel.isSecret = isPrivate;

			File photoFile = new File(photoModel.uri);

			if (isPrivate) {
				File privateFile = new File(context.getDir(FolderConstants.PRIVATE_FOLDER, Context.MODE_PRIVATE), photoModel.displayName);

				movePhoto(photoFile, privateFile);

				photoModel.formerUri = photoModel.uri;
				photoModel.uri = privateFile.getPath();
			}
			else {
				File formerLocation = new File(photoModel.formerUri);

				movePhoto(photoFile, formerLocation);

				photoModel.formerUri = null;
				photoModel.uri = formerLocation.getPath();
			}

			saveIsPrivatePreference(photoModel, isPrivate);

			livePhotoModel.setValue(photoModel);
		}
	}

	public int copyPhoto(String src, String dest) throws IOException {
		ContentResolver contentResolver = context.getContentResolver();
		try (FileInputStream from = src.contains("content:/") ?
				                            (FileInputStream) contentResolver.openInputStream(Uri.parse(src)) :
				                            new FileInputStream(src);
		     FileOutputStream to = dest.contains("content:/") ?
				                           (FileOutputStream) contentResolver.openOutputStream(Uri.parse(dest)) :
				                           new FileOutputStream(dest)) {
			return IOUtils.copy(from, to);
		}
	}

	private void refreshMediaStore() {
		MediaScannerConnection.scanFile(context,
				new String[]{Environment.getExternalStorageDirectory().toString()},
				null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.d("ExternalStorage", "Scanned " + path + ":");
						Log.d("ExternalStorage", "-> uri=" + uri);
					}
				});
	}

	public boolean deletePhotoWithoutAsking(String uriStr) {
		ContentResolver contentResolver = context.getContentResolver();

		boolean result;

		if (uriStr.contains("content:/")) {
			int deletedBytes =
					contentResolver.delete(Uri.parse(uriStr), null, null);
			result = deletedBytes > 0;
		}
		else {
			result = new File(uriStr).delete();
		}

		this.refreshMediaStore();

		return result;
	}

	/**
	 * Move file to new location
	 *
	 * @param from will be delete after done
	 * @param to   destination
	 */
	private void movePhoto(File from, File to) throws IOException {
		int bytesNum = copyPhoto(from.getPath(), to.getPath());
		Log.d("PhotoViewTextClick", "Copy " + bytesNum + " bytes");

		boolean isDeleted = deletePhotoWithoutAsking(from.getPath());
		Log.d("PhotoViewTextClick", "Is image deleted? " + isDeleted);
	}

	private void saveIsFavorite(PhotoModel photoModel, boolean isFavorite) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(PhotoPreferences.favoritePreferenceOf(photoModel.displayName), isFavorite);
		editor.apply();

		Log.d("PhotoViewTextClick",
				"PhotoUri=" + photoModel.uri + ", " + "isFavorite=" + photoModel.isFavorite);
	}

	private void saveIsPrivatePreference(PhotoModel photoModel, boolean isPrivate) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		if (isPrivate) {
			editor.putString(PhotoPreferences.privatePreferenceOf(photoModel.displayName),
					new Gson().toJson(photoModel));

			Log.d("PhotoViewTextClick", "Save to object to preferences: " + PhotoPreferences.PRIVATE + ":" + photoModel.displayName);
		}
		else {
			editor.remove(photoModel.uri + PhotoPreferences.PRIVATE);
			Log.d("PhotoViewTextClick", "Remove object from preferences:  " + PhotoPreferences.PRIVATE + ":" + photoModel.displayName);
		}

		Log.d("PhotoViewTextClick",
				"PhotoUri=" + photoModel.uri + ", " + "isPrivate=" + photoModel.isSecret);

		editor.apply();
	}

	public void setImageAsBackground(PhotoModel photoModel) throws IOException {
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
		InputStream inputStream = new FileInputStream(photoModel.uri);

		wallpaperManager.setStream(inputStream);

		inputStream.close();
	}

	public String getPath(final Context context, final Uri uri) {

		@SuppressLint("ObsoleteSdkInt") final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				}
				else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				}
				else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public void insertImage(String src, String dest, PhotoModel photoModel) throws IOException {
		copyPhoto(src, dest);

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.DISPLAY_NAME, photoModel.displayName);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
		// Add the date meta data to ensure the image is added at the front of the gallery
		values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
		values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());

		Uri trashUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		this.deletePhotoWithoutAsking(trashUri.toString());
	}

	public void saveImageLocationPreference(PhotoModel photoModel, String location) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(PhotoPreferences.locationPreferenceOf(photoModel.displayName), location);
		editor.apply();
	}
}
