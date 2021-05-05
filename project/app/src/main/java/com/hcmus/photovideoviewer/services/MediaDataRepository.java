package com.hcmus.photovideoviewer.services;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.CodeBoy.MediaFacer.mediaHolders.pictureFolderContent;
import com.CodeBoy.MediaFacer.mediaHolders.videoContent;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.gson.Gson;
import com.hcmus.photovideoviewer.MainApplication;
import com.hcmus.photovideoviewer.constants.FolderConstants;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.ExploreModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MediaDataRepository {
	@SuppressLint("StaticFieldLeak")
	private static MediaDataRepository instance = null;
	private final Context context;
	private final ArrayList<PhotoModel> photoModels = new ArrayList<>();
	private final ArrayList<VideoModel> videoModels = new ArrayList<>();
	private final ArrayList<AlbumModel> albumModels = new ArrayList<>();
	private final ArrayList<ExploreModel> exploreModels = new ArrayList<>();
	private final Python py = Python.getInstance();
	private MediaDataRepository() {
		context = MainApplication.getContext();
	}

	public static MediaDataRepository getInstance() {
		if (instance == null) {
			instance = new MediaDataRepository();
		}
		return instance;
	}

	@RequiresApi(api = Build.VERSION_CODES.P)
	public void fetchData() {
		try {
			this.fetchPhotos();
			this.fetchVideos();
			this.fetchAlbums();
			this.fetchExplores();
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

	public ArrayList<ExploreModel> getExploreModels(){return exploreModels;}

	public ArrayList<PhotoModel> fetchPhotos() {
		photoModels.clear();

		ArrayList<PhotoModel> externalPhotoModels = queryPhoto(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		ArrayList<PhotoModel> internalPhotoModels =
				this.getPrivatePhotos(context.getDir(FolderConstants.PRIVATE_FOLDER, Context.MODE_PRIVATE));

		photoModels.addAll(externalPhotoModels);
		photoModels.addAll(internalPhotoModels);

		Log.d("Images", "Found " + photoModels.size() + " photos");
		return this.photoModels;
	}


	private ArrayList<PhotoModel> queryPhoto(Uri whereToLook) {
		ArrayList<PhotoModel> models = new ArrayList<>();

		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_MODIFIED,
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.DATA
		};

		ContentResolver contentResolver = context.getContentResolver();
		try (Cursor cursor = contentResolver.query(whereToLook,
				projection,
				null,
				null,
				MediaStore.Images.Media.DATE_MODIFIED + " DESC")) {
			int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
			int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
			int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
			int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			while (cursor.moveToNext()) {
				long _id = cursor.getLong(idColumn);
				String name = cursor.getString(nameColumn);
				Long _size = cursor.getLong(sizeColumn);
				Long date = cursor.getLong(dateColumn);
				String filePath = cursor.getString(pathColumn);

//				String
				PhotoModel photoModel = new PhotoModel() {
					{
						id = _id;
						displayName = name;
						size = _size;
						dateModified = new Date(date * 1000);
//						uri = ContentUris.withAppendedId(whereToLook, id).toString();
						uri = filePath;
						isFavorite = getIsFavorite(this);
						isSecret = MediaStore.Images.Media.INTERNAL_CONTENT_URI == whereToLook;
					}
				};

				models.add(photoModel);
			}
		}

		return models;
	}

	/**
	 * Check if this photo is in Favorite list
	 */
	private boolean getIsFavorite(PhotoModel photoModel) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);

		return sharedPreferences.getBoolean(
				PhotoPreferences.favoritePreferenceOf(photoModel.displayName),
				false);
	}

	private ArrayList<PhotoModel> getPrivatePhotos(File directory) {
		ArrayList<PhotoModel> internalPhotoModels = new ArrayList<>();

		if (directory != null && directory.exists() && directory.isDirectory()) {
			File[] imageFiles = directory.listFiles();

			if (imageFiles != null) {
				SharedPreferences sharedPreferences =
						context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);
				Gson gson = new Gson();
				for (File imageFile : imageFiles) {
					String photoJson = sharedPreferences.getString(
							PhotoPreferences.privatePreferenceOf(imageFile.getName()), ""
					);

					PhotoModel photoModel = gson.fromJson(photoJson, PhotoModel.class);

					if (photoModel != null) {
						internalPhotoModels.add(photoModel);
					}
				}
				;
			}
		}

		return internalPhotoModels;
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

		ArrayList<pictureContent> allPhotosAlbum;
		ArrayList<videoContent> allVideosAlbum;

		ArrayList<pictureFolderContent> pictureFolders = new ArrayList<>(MediaFacer.withPictureContex(context).getPictureFolders());
		for (int i = 0; i < pictureFolders.size(); i++) {
			int index = i;
			allPhotosAlbum = MediaFacer.withPictureContex(context).getAllPictureContentByBucket_id(pictureFolders.get(index).getBucket_id());
			allVideosAlbum = MediaFacer.withVideoContex(context).getAllVideoContentByBucket_id(pictureFolders.get(index).getBucket_id());
			ArrayList<pictureContent> finalAllPhotos = allPhotosAlbum;
			ArrayList<videoContent> finalAllVideos = allVideosAlbum;

			AlbumModel albumModel = new AlbumModel() {
				{
					albumName = pictureFolders.get(index).getFolderName();
					quantity = finalAllPhotos.size() + finalAllVideos.size();
					imageUrl = finalAllPhotos.get(finalAllPhotos.size()-1).getPictureId();
				}
			};
			albumModels.add(albumModel);
		}
		Log.d("Size of Album: ", "" + albumModels.get(0));
		//mark favorite
		SharedPreferences sharePref =  this.context.getSharedPreferences("Photos",Context.MODE_PRIVATE);
		Map<String, ?> allEntries = sharePref.getAll();
		if(!allEntries.isEmpty()){
			ArrayList<PhotoModel> photoModels = this.getPhotoModels();
			AlbumModel albumFavorite = new AlbumModel();
			int sizeAlbum = 0;
			for(int i = 0; i < photoModels.size(); i++){
				if(photoModels.get(i).isFavorite == true){
					sizeAlbum++;
				}
			}
			int finalSizeAlbum = sizeAlbum;
			albumFavorite = new AlbumModel() {
				{
					imageUrl = photoModels.get(finalSizeAlbum - 1).id;
					albumName = "Favourites";
					quantity = finalSizeAlbum;
				}
			};
			albumModels.add(albumFavorite);
		}
		//mark private
		//next
		return albumModels;
	}
	@RequiresApi(api = Build.VERSION_CODES.P)
	public ArrayList<ExploreModel> fetchExplores(){
		exploreModels.clear();
		List<PyObject> data_Face = recognizePerson();
		for(int i = 0; i < data_Face.size(); i++){
			byte data[] = android.util.Base64.decode(data_Face.get(i).toString(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			ExploreModel exploreModel = new ExploreModel(){
				{
					bitmapAvatar = bmp;
				}
			};
			exploreModels.add(exploreModel);
		}

//		exploreModels.add(exploreModel);
//		exploreModels.add(exploreModel);
//		exploreModels.add(exploreModel);
//		exploreModels.add(exploreModel);
//		exploreModels.add(exploreModel);
//		exploreModels.add(exploreModel);
//		exploreModels.add(exploreModel);
		//recognizePerson();
		return exploreModels;
	}
	@RequiresApi(api = Build.VERSION_CODES.P)
	public List<PyObject> recognizePerson(){

		Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//		long idPerson1 = photoModels.get(0).id;
//		String uriPerson1 = ContentUris.withAppendedId(_uri, idPerson1).toString();
//
//		long idPerson2 = photoModels.get(1).id;
//		String uriPerson2 = ContentUris.withAppendedId(_uri, idPerson2).toString();

//		Bitmap bitmap = null;
//		Bitmap bitmap2 = null;
//		try {
//			bitmap = getBitmapFromUri(Uri.parse(uriPerson1));//getBitmap(context.getContentResolver(), Uri.parse(uriPerson1));
//			bitmap2 = getBitmapFromUri(Uri.parse(uriPerson2));
//		}
//	   catch (Exception e) {
//	   }
//		String imageString = getStringImage(bitmap);
//		String imageString2 = getStringImage(bitmap2);

		//ArrayList<PhotoModel> dataRegonize = new ArrayList<PhotoModel>();
		PyObject pyo = py.getModule("myscript");

//		PyObject obj = pyo.callAttr("main", imageString, imageString2);
//		Log.d("Python Return: ", obj.toString());
//		String result = obj.toString();
//		PyObject obj2 = pyo.callAttr("main_test", imageString);
		//Log.d("Python Return Test: ", obj2.toString());
//		String str = obj2.toString();
//		String str = getStringImage(bitmap);

		//convert bitmap
//		byte data[] = android.util.Base64.decode(str, Base64.DEFAULT);
//		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

		//ti mo
		String strCount = "";
		for(int i = 0; i < photoModels.size(); i++){
			Bitmap bitmap = null;
			long idPerson = photoModels.get(i).id;
			String uriPerson = ContentUris.withAppendedId(_uri, idPerson).toString();
			try {
				bitmap = getBitmapFromUri(Uri.parse(uriPerson));
			}
			catch (Exception e) {
			}
			String imageString = getStringImage(bitmap);
			PyObject obj2 = pyo.callAttr("main_test", imageString);
			String str = obj2.toString();
			System.out.println("abc");
		}
		PyObject obj3 = pyo.callAttr("get_data_face");
		List<PyObject> dataFace = new ArrayList<PyObject>();
		dataFace = obj3.asList();
		return dataFace;
//		String sssss = dataFace.get(0).toString();
//		String ssssss = dataFace.get(1).toString();
//		for(int i = 0; i < dataFace.size(); i++){
//			exploreModels.add()
//		}
//		System.out.println("abc");
		//
//		Bitmap bitmap = null;
//		long idPerson = photoModels.get(1).id;
//		Bitmap bitmap2 = null;
//		long idPerson2 = photoModels.get(7).id;
//		String uriPerson = ContentUris.withAppendedId(_uri, idPerson).toString();
//		String uriPerson2 = ContentUris.withAppendedId(_uri, idPerson2).toString();
//		try {
//			bitmap = getBitmapFromUri(Uri.parse(uriPerson));
//			bitmap2 = getBitmapFromUri(Uri.parse(uriPerson2));
//		}
//		catch (Exception e) {
//		}
//		String imageString = getStringImage(bitmap);
//		String imageString2 = getStringImage(bitmap2);
//
//		PyObject obj2 = pyo.callAttr("main", imageString, imageString2);
//		String str = obj2.toString();
//		System.out.println("abc");

//		byte data[] = android.util.Base64.decode(str, Base64.DEFAULT);
//		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//		return null;
//		return dataRegonize;
	}
	private Bitmap getBitmapFromUri(Uri uri) throws IOException {
		ParcelFileDescriptor parcelFileDescriptor =
				context.getContentResolver().openFileDescriptor(uri, "r");
		FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
		Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
		parcelFileDescriptor.close();
		return image;
	}
	private String getStringImage(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return  encodedImage;
	}
}
