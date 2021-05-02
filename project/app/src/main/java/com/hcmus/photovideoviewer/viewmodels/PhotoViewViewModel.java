package com.hcmus.photovideoviewer.viewmodels;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public static void delete(final Activity activity, final Uri[] uriList, final int requestCode)
			throws SecurityException, IntentSender.SendIntentException, IllegalArgumentException {
		final ContentResolver resolver = activity.getContentResolver();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			// WARNING: if the URI isn't a MediaStore Uri and specifically
			// only for media files (images, videos, audio), the request
			// will throw an IllegalArgumentException, with the message:
			// 'All requested items must be referenced by specific ID'

			// No need to handle 'onActivityResult' callback, when the system returns
			// from the user permission prompt the files will be already deleted.
			// Multiple 'owned' and 'not-owned' files can be combined in the
			// same batch request. The system will automatically delete them using the
			// using the same prompt dialog, making the experience homogeneous.

			final List<Uri> list = new ArrayList<>();
			Collections.addAll(list, uriList);

			final PendingIntent pendingIntent = MediaStore.createDeleteRequest(resolver, list);
			activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
		}
		else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
			try {
				// In Android == Q a RecoverableSecurityException is thrown if not-owned.
				// For a batch request the deletion will stop at the failed not-owned
				// file, so you may want to restrict deletion in Android Q to only
				// 1 file at a time, to make the experience less ugly.
				// Fortunately this gets solved in Android R.

				for (final Uri uri : uriList) {
					resolver.delete(uri, null, null);
				}
			} catch (RecoverableSecurityException ex) {
				final IntentSender intent = ex.getUserAction()
						                            .getActionIntent()
						                            .getIntentSender();

				// IMPORTANT: still need to perform the actual deletion
				// as usual, so again getContentResolver().delete(...),
				// in your 'onActivityResult' callback, as in Android Q
				// all this extra code is necessary 'only' to get the permission,
				// as the system doesn't perform any actual deletion at all.
				// The onActivityResult doesn't have the target Uri, so you
				// need to catch it somewhere.
				activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);
			}
		}
		else {
			// As usual for older APIs

			for (final Uri uri : uriList) {
				resolver.delete(uri, null, null);
			}
		}
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

	private int copyPhoto(String src, String dest) throws IOException {
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

	private boolean deletePhotoWithoutAsking(String uriStr) {
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

		if (isDeleted) {
			refreshMediaStore();
		}
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

	public void deleteImage(PhotoModel photoModel, int requestCode) throws PendingIntent.CanceledException, IntentSender.SendIntentException {
		final ContentResolver resolver = activity.getContentResolver();

		final List<Uri> uriList = new ArrayList<>();
		uriList.add(Uri.parse(photoModel.uri));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			// WARNING: if the URI isn't a MediaStore Uri and specifically
			// only for media files (images, videos, audio), the request
			// will throw an IllegalArgumentException, with the message:
			// 'All requested items must be referenced by specific ID'

			// No need to handle 'onActivityResult' callback, when the system returns
			// from the user permission prompt the files will be already deleted.
			// Multiple 'owned' and 'not-owned' files can be combined in the
			// same batch request. The system will automatically delete them using the
			// using the same prompt dialog, making the experience homogeneous.


			final PendingIntent pendingIntent = MediaStore.createDeleteRequest(resolver, uriList);
			activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
		}
		else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
			try {
				// In Android == Q a RecoverableSecurityException is thrown if not-owned.
				// For a batch request the deletion will stop at the failed not-owned
				// file, so you may want to restrict deletion in Android Q to only
				// 1 file at a time, to make the experience less ugly.
				// Fortunately this gets solved in Android R.

				for (final Uri uri : uriList) {
					resolver.delete(uri, null, null);
				}
			} catch (RecoverableSecurityException ex) {
				final IntentSender intent = ex.getUserAction()
						                            .getActionIntent()
						                            .getIntentSender();

				// IMPORTANT: still need to perform the actual deletion
				// as usual, so again getContentResolver().delete(...),
				// in your 'onActivityResult' callback, as in Android Q
				// all this extra code is necessary 'only' to get the permission,
				// as the system doesn't perform any actual deletion at all.
				// The onActivityResult doesn't have the target Uri, so you
				// need to catch it somewhere.
				activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);
			}
		}
		else {
			// As usual for older APIs

			for (final Uri uri : uriList) {
				resolver.delete(uri, null, null);
			}
		}
	}
}
