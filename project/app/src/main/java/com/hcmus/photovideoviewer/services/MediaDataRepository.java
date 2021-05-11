package com.hcmus.photovideoviewer.services;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.gson.Gson;
import com.hcmus.photovideoviewer.MainApplication;
import com.hcmus.photovideoviewer.constants.FolderConstants;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.constants.VideoPreferences;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.ExploreModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
	private Python py = Python.getInstance();
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
			//this.fetchAlbums();
			//this.fetchExplores();
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

	public PhotoModel queryPhotoModel(Context context, Uri androidProviderUri) {
		PhotoModel photoModel = null;

		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_MODIFIED,
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.DATA,
		};

		ContentResolver contentResolver = context.getContentResolver();
		try (Cursor cursor = contentResolver.query(androidProviderUri,
				projection,
				null,
				null,
				MediaStore.Images.Media.DATE_MODIFIED + " DESC")) {
			int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
			int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
			int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
			int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);



			if (cursor.moveToFirst()) {
				long _id = cursor.getLong(idColumn);
				String name = cursor.getString(nameColumn);
				Long _size = cursor.getLong(sizeColumn);
				Long date = cursor.getLong(dateColumn);
				String filePath = cursor.getString(pathColumn);

				photoModel = new PhotoModel() {
					{
						id = _id;
						displayName = name;
						size = _size;
						dateModified = new Date(date * 1000);
//						uri = ContentUris.withAppendedId(whereToLook, id).toString();
						uri = filePath;
					}
				};
			}
		}

		return photoModel;
	}

	private ArrayList<PhotoModel> queryPhoto(Uri whereToLook) {
		ArrayList<PhotoModel> models = new ArrayList<>();

		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_MODIFIED,
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.DATA,
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
					}
				};

				photoModel.isFavorite = getIsFavoritePhoto(photoModel);
				photoModel.location = getImageLocation(photoModel);

				models.add(photoModel);
			}
		}

		return models;
	}

	private String getImageLocation(PhotoModel photoModel) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);
		return sharedPreferences.getString(PhotoPreferences.locationPreferenceOf(photoModel.displayName), "");
	}

	/**
	 * Check if this photo is in Favorite list
	 */
	private boolean getIsFavoritePhoto(PhotoModel photoModel) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(PhotoPreferences.PHOTOS, Context.MODE_PRIVATE);

		return sharedPreferences.getBoolean(
				PhotoPreferences.favoritePreferenceOf(photoModel.displayName),
				false);
	}

	private boolean getIsFavoriteVideo(VideoModel videoModel) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(VideoPreferences.VIDEOS, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(VideoPreferences.favoritePreferenceOf(videoModel), false);
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
				MediaStore.Video.Media.DURATION,
				MediaStore.Video.Media.DATA,
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
			int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

			while (cursor.moveToNext()) {
				long _id = cursor.getLong(idColumn);
				String name = cursor.getString(nameColumn);
				Long _size = cursor.getLong(sizeColumn);
				Long date = cursor.getLong(dateColumn);
				Long _duration = cursor.getLong(durationColumn);
				String filePath = cursor.getString(pathColumn);

				VideoModel videoModel = new VideoModel() {
					{
						id = _id;
						displayName = name;
						size = _size;
						dateModified = new Date(date * 1000);
						duration = _duration / 1000;
//						uri = ContentUris.withAppendedId(_uri, id);
						uri = Uri.parse(filePath);
					}
				};

				videoModel.isFavorite = this.getIsFavoriteVideo(videoModel);

				videoModels.add(videoModel);
			}
		}

		Log.d("Videos", "Found " + videoModels.size() + " Videos");

		return this.videoModels;
	}

	public ArrayList<AlbumModel> fetchAlbums(ArrayList<PhotoModel> photoModelsInput, ArrayList<VideoModel> videoModelsInput) {
		albumModels.clear();
//		SharedPreferences sharedPrefVideo = this.context.getSharedPreferences("Videos", Context.MODE_PRIVATE);
//		Map<String, ?> allVideo = sharedPrefVideo.getAll();
//		SharedPreferences sharePref =  this.context.getSharedPreferences("Photos",Context.MODE_PRIVATE);
//		Map<String, ?> allEntries = sharePref.getAll();


			Map<String, Integer> mapQuantityPhoto = new HashMap<>();
			Map<String, Long> mapIdAlbum = new HashMap<>();
			Map<String, Integer> mapQuantityVideo = new HashMap<>();
			ArrayList<String> ListNameAlbum = new ArrayList<String>();
			int sizeAlbumFavorite = 0;
			int sizeVideoFavorite = 0;
			long idAvatarFavorite = 0;
			for(int i = 0; i < photoModelsInput.size(); i++){
				String[] getAlbumFromPhoto = photoModelsInput.get(i).uri.split("/");
				String newAlbum = getAlbumFromPhoto[getAlbumFromPhoto.length - 2];
				if(!checkExistAlbum(ListNameAlbum, newAlbum)){
					mapQuantityPhoto.put(newAlbum, 1);
					mapQuantityVideo.put(newAlbum, 0);
					ListNameAlbum.add(newAlbum);
					mapIdAlbum.put(newAlbum, photoModelsInput.get(i).id);
				}
				else{
					int count = mapQuantityPhoto.get(newAlbum);
					mapQuantityPhoto.put(newAlbum, ++count);
					mapQuantityVideo.put(newAlbum, 0);
					mapIdAlbum.put(newAlbum, photoModelsInput.get(i).id);
				}
				if(photoModelsInput.get(i).isFavorite){
					sizeAlbumFavorite++;
					idAvatarFavorite = photoModelsInput.get(i).id;
				}
			}
		//video
		String debugg = "";
		for(int i = 0; i < videoModelsInput.size(); i++){
			String fullPath = String.valueOf(videoModelsInput.get(i).uri);
			String[] getAlbumFromVideo = fullPath.split("/");
			String newAlbum = getAlbumFromVideo[getAlbumFromVideo.length - 2];
			if(!checkExistAlbum(ListNameAlbum, newAlbum)){
				ListNameAlbum.add(newAlbum);
				mapQuantityVideo.put(newAlbum, 1);
				mapIdAlbum.put(newAlbum, videoModelsInput.get(i).id);
			}
			else{
				int count = mapQuantityVideo.get(newAlbum);
				mapQuantityVideo.put(newAlbum, ++count);
			}
			if(videoModelsInput.get(i).isFavorite){
				sizeVideoFavorite++;
				if(idAvatarFavorite == 0){
					idAvatarFavorite = videoModelsInput.get(i).id;
				}
			}
		}
			int finalSizeFavorite = sizeAlbumFavorite;
			if(finalSizeFavorite > 0){
				AlbumModel albumFavorite = new AlbumModel();
				long finalIdAvatarFavorite = idAvatarFavorite;
				int finalSizeVideoFavorite = sizeVideoFavorite;
				albumFavorite = new AlbumModel() {
					{
						imageUrl = finalIdAvatarFavorite;
						albumName = "Favourites";
						quantityPhoto = finalSizeFavorite;
						quantityVideo = finalSizeVideoFavorite;
					}
				};
				albumModels.add(albumFavorite);
			}
//		}
		//mark private

		//next
		for(int i = 0; i < mapQuantityPhoto.size(); i++){
			String nameOfAlbum = ListNameAlbum.get(i);
			if(nameOfAlbum.equals("app_PrivatePictures")){
				nameOfAlbum = "Private";
			}
			String finalNameOfAlbum = nameOfAlbum;
			int finalI = i;
			AlbumModel albumModel = new AlbumModel() {
				{
					albumName = finalNameOfAlbum;
					quantityPhoto = mapQuantityPhoto.get(ListNameAlbum.get(finalI));
					imageUrl = mapIdAlbum.get(ListNameAlbum.get(finalI));
					quantityVideo = mapQuantityVideo.get(ListNameAlbum.get(finalI));
				}
			};
			albumModels.add(albumModel);
		}
		System.out.println("Debug");
		return albumModels;
	}
	public ArrayList<ExploreModel> fetchExplores(ArrayList<PhotoModel> photoModelsInput){
		exploreModels.clear();
		//ArrayList<PhotoModel> photoModelsInput = this.getPhotoModels();

		List<PyObject> data_Face = recognizePerson(photoModelsInput);
		for(int i = 0; i < data_Face.size(); i++){
			byte data[] = android.util.Base64.decode(data_Face.get(i).toString(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            int finalI = i;
            ExploreModel exploreModel = new ExploreModel(){
				{
				    idPerson = finalI + 10;
					bitmapAvatar = bmp;
				}
			};
			exploreModels.add(exploreModel);
		}
		return exploreModels;
	}
	public List<PyObject> recognizePerson(ArrayList<PhotoModel> photoModelsInput){
		Log.d("RecognizePerson:", "" + photoModelsInput.size());
		SharedPreferences sharedPrefStringPhoto = this.context.getSharedPreferences("StringPhoto", Context.MODE_PRIVATE);
		SharedPreferences sharedPrefRecognize = this.context.getSharedPreferences("Recognize", Context.MODE_PRIVATE);
		SharedPreferences sharedPreFaceDetect = this.context.getSharedPreferences("FaceDetect", Context.MODE_PRIVATE);
		Map<String, ?> mapPrefRecognize = sharedPrefRecognize.getAll();
		Map<String, ?> mapPrefStringPhoto = sharedPrefStringPhoto.getAll();
		Map<String, ?> mapPrefFaceDetect = sharedPreFaceDetect.getAll();
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
        //content://media/external/images/media/57
		String strCount = "";
		List<PyObject> dataFace = new ArrayList<PyObject>();
		if(mapPrefFaceDetect.size() > 0){
			pyo.callAttr("reset_data");
			for(int i = 0; i < mapPrefFaceDetect.size(); i++){
				pyo.callAttr("set_data_face", mapPrefFaceDetect.get(i + ""));
			}
			PyObject obj3 = pyo.callAttr("get_data_face");
			dataFace = obj3.asList();
		}
		int flag = 0;
		for(int i = 0; i < photoModelsInput.size(); i++){
			if (photoModelsInput.get(i).isSecret)
				continue;
			String imageString = "";
			if(checkExistRecognizePerson(mapPrefRecognize, photoModelsInput.get(i).id)){
				//imageString = mapPrefStringPhoto.get(photoModelsInput.get(i).id + "").toString();
				continue;
			}
			else {
				Bitmap bitmap = null;
				long idPerson = photoModelsInput.get(i).id;
				Uri uriPerson = ContentUris.withAppendedId(_uri, idPerson);
				try {
					//bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriPerson);
					bitmap = getBitmapFormUri(context, uriPerson);
					//int degree = getBitmapDegree(file.getAbsolutePath());
				} catch (Exception e) {
					Log.e("RecognizePerson Error: ", "Error");
				}
				imageString = getStringImage(bitmap); //cham
				String debugg = "";
				System.out.println("abc");
				SharedPreferences.Editor editorListRecognize = sharedPrefRecognize.edit();
				editorListRecognize.putBoolean(photoModelsInput.get(i).id + "", true);
				editorListRecognize.commit();
//				SharedPreferences.Editor editorListPhotoString = sharedPrefStringPhoto.edit();
//				editorListPhotoString.putString(photoModelsInput.get(i).id + "", imageString);
//				editorListPhotoString.commit();
				flag = 1;
			}
			PyObject obj2 = pyo.callAttr("main_test", imageString); //anh so 14
			String str = obj2.toString();
		}
		if(flag == 1){
			PyObject obj3 = pyo.callAttr("get_data_face");
			dataFace = obj3.asList();
			for(int i = 0; i < dataFace.size(); i++){
				SharedPreferences.Editor editDetectFace = sharedPreFaceDetect.edit();
				editDetectFace.putString(i + "", dataFace.get(i).toString());
				editDetectFace.commit();
			}
		}

		return dataFace;
	}
public static Bitmap getBitmapFormUri(Context ac, Uri uri) throws FileNotFoundException, IOException {
	InputStream input = ac.getContentResolver().openInputStream(uri);
	BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
	onlyBoundsOptions.inJustDecodeBounds = true;
	onlyBoundsOptions.inDither = true;//optional
	onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
	BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
	input.close();
	int originalWidth = onlyBoundsOptions.outWidth;
	int originalHeight = onlyBoundsOptions.outHeight;
	if ((originalWidth == -1) || (originalHeight == -1))
		return null;
	//Image resolution is based on 480x800
	float hh = 800f;//The height is set as 800f here
	float ww = 480f;//Set the width here to 480f
	//Zoom ratio. Because it is a fixed scale, only one data of height or width is used for calculation
	int be = 1;//be=1 means no scaling
	if (originalWidth > originalHeight && originalWidth > ww) {//If the width is large, scale according to the fixed size of the width
		be = (int) (originalWidth / ww);
	} else if (originalWidth < originalHeight && originalHeight > hh) {//If the height is high, scale according to the fixed size of the width
		be = (int) (originalHeight / hh);
	}
	if (be <= 0)
		be = 1;
	//Proportional compression
	BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	bitmapOptions.inSampleSize = be;//Set scaling
	bitmapOptions.inDither = true;//optional
	bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
	input = ac.getContentResolver().openInputStream(uri);
	Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
	input.close();

	return compressImage(bitmap);//Mass compression again
}
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//Quality compression method, here 100 means no compression, store the compressed data in the BIOS
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {  //Cycle to determine if the compressed image is greater than 100kb, greater than continue compression
			baos.reset();//Reset the BIOS to clear it
			//First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//Here, the compression options are used to store the compressed data in the BIOS
			options -= 10;//10 less each time
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//Store the compressed data in ByteArrayInputStream
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//Generate image from ByteArrayInputStream data
		return bitmap;
	}
	private String getStringImage(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return  encodedImage;
	}
	private boolean checkExistAlbum(ArrayList<String> albumModels, String newAlbum){
		for(int i =0; i < albumModels.size(); i++){
			if(albumModels.get(i).equals(newAlbum)){
				return true;
			}
		}
		return false;
	}
	private boolean checkExistRecognizePerson(Map<String, ?> mapPhotoRegconize, Long id){
		try{
			String a = mapPhotoRegconize.get(id + "").toString();
			return true;
		}catch (Exception e){
			return false;
		}
	}
}
