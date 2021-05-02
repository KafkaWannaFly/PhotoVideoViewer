package com.hcmus.photovideoviewer.viewmodels;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hcmus.photovideoviewer.constants.FolderConstants;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
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

//			File photoFile = new File(photoModel.uri);

//			if (isPrivate) {
//				File privateFile = new File(context.getDir(FolderConstants.PRIVATE_FOLDER, Context.MODE_PRIVATE), photoModel.displayName);
//
//				moveFile(photoFile, privateFile);
//
//				photoModel.formerUri = photoModel.uri;
//				photoModel.uri = privateFile.getPath();
//			}
//			else {
//				File formerLocation = new File(photoModel.formerUri);
////				Files.move(photoFile.toPath(), formerLocation.toPath());
//				moveFile(photoFile, formerLocation);
//
//				photoModel.formerUri = null;
//				photoModel.uri = formerLocation.getPath();
//			}

			saveIsPrivatePreference(photoModel, isPrivate);

			Log.d("PhotoViewTextClick", "Move file to " + photoModel.uri);


			livePhotoModel.setValue(photoModel);
		}
	}

	/**
	 * Move file to new location
	 * @param from will be delete after done
	 * @param to   destination
	 */
	private void moveFile(File from, File to) throws IOException {
//		OutputStream fromStream = context.getContentResolver().openOutputStream(Uri.parse(from.getPath()));
//		InputStream toStream = context.getContentResolver().openInputStream(Uri.parse(to.getPath()));
		FileUtils.moveFile(from, to);
//		try (FileOutputStream fos = new FileOutputStream(from);
//		     FileInputStream reader = new FileInputStream(to)) {
//			byte[] data = new byte[(int) from.length()];
//			int readBytes = reader.read(data, 0, data.length);
//
//			fos.write(data);
//
//			Log.d("PhotoViewTextClick", "Read and write " + readBytes + " bytes");
//
//			if(from.delete()) {
//				Log.d("PhotoViewTextClick", "Deleted " + from.getName());
//			}
//			else {
//				Log.d("PhotoViewTextClick", "Not able to delete " + from.getName());
//			}
//		}
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

		if(isPrivate) {
			editor.putString(PhotoPreferences.privatePreferenceOf(photoModel.displayName),
					new Gson().toJson(photoModel));

			Log.d("PhotoViewTextClick", "Save to object to preferences: " + PhotoPreferences.PRIVATE+":"+photoModel.displayName);
		}
		else {
			editor.remove(photoModel.uri + PhotoPreferences.PRIVATE);
			Log.d("PhotoViewTextClick", "Remove object from preferences:  " + PhotoPreferences.PRIVATE+":"+photoModel.displayName);
		}

		Log.d("PhotoViewTextClick",
				"PhotoUri=" + photoModel.uri + ", " + "isPrivate=" + photoModel.isSecret);

		editor.apply();
	}

	public void deleteImage(PhotoModel photoModel, int requestCode) throws PendingIntent.CanceledException, IntentSender.SendIntentException {
		final ContentResolver resolver = activity.getContentResolver();

		final List<Uri> uriList = new ArrayList<>();
		uriList.add(Uri.parse(photoModel.uri));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
		{
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
		else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
		{
			try
			{
				// In Android == Q a RecoverableSecurityException is thrown if not-owned.
				// For a batch request the deletion will stop at the failed not-owned
				// file, so you may want to restrict deletion in Android Q to only
				// 1 file at a time, to make the experience less ugly.
				// Fortunately this gets solved in Android R.

				for (final Uri uri : uriList)
				{
					resolver.delete(uri, null, null);
				}
			}
			catch (RecoverableSecurityException ex)
			{
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
		else
		{
			// As usual for older APIs

			for (final Uri uri : uriList)
			{
				resolver.delete(uri, null, null);
			}
		}
	}

	public static void delete(final Activity activity, final Uri[] uriList, final int requestCode)
			throws SecurityException, IntentSender.SendIntentException, IllegalArgumentException
	{
		final ContentResolver resolver = activity.getContentResolver();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
		{
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
		else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
		{
			try
			{
				// In Android == Q a RecoverableSecurityException is thrown if not-owned.
				// For a batch request the deletion will stop at the failed not-owned
				// file, so you may want to restrict deletion in Android Q to only
				// 1 file at a time, to make the experience less ugly.
				// Fortunately this gets solved in Android R.

				for (final Uri uri : uriList)
				{
					resolver.delete(uri, null, null);
				}
			}
			catch (RecoverableSecurityException ex)
			{
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
		else
		{
			// As usual for older APIs

			for (final Uri uri : uriList)
			{
				resolver.delete(uri, null, null);
			}
		}
	}
}
