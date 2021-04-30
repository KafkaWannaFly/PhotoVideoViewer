package com.hcmus.photovideoviewer.views;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.hcmus.photovideoviewer.adapters.AlbumAdapter;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.viewmodels.AlbumsViewModel;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class AlbumsFragment extends Fragment {
	enum LayoutManagerType {
		GRID_LAYOUT_MANAGER,
		LINEAR_LAYOUT_MANAGER
	}
	private AlbumsViewModel mViewModel;
	private RecyclerView mRecyclerView;
	private GridLayoutManager gridLayoutManager;
	protected RecyclerView.LayoutManager mLayoutManager;
	protected LayoutManagerType mCurrentLayoutManagerType;
	//	protected String[] mDataset;
	private AlbumAdapter albumAdapter;
	private AlbumsViewModel albumsViewModel;
	public static AppBarViewModel appBarViewModel = null;

	public AlbumsFragment(AppBarViewModel appBarViewModel){
		this.appBarViewModel = appBarViewModel;

	}
//	public static AlbumsFragment newInstance() {
//		return new AlbumsFragment();
//	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize dataset, this data would usually come from a local content provider or
		// remote server.

	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.albums_fragment, container, false);
		rootView.setTag("RecyclerViewFragment");
		mRecyclerView = rootView.findViewById(R.id.album_recycle_view);
//		mLayoutManager = new LinearLayoutManager(getActivity());
		appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
			mLayoutManager = new GridLayoutManager(getActivity(), columnSpan);
			mRecyclerView.setLayoutManager(mLayoutManager);
//			mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
		});

		if (savedInstanceState != null) {
			// Restore saved layout manager type.
			mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
					.getSerializable("layoutManager");
		}
//		setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
		ArrayList<PhotoModel> dataPhotoFavorite = new ArrayList<PhotoModel>();
		SharedPreferences sharePref = getActivity().getSharedPreferences("Photos",Context.MODE_PRIVATE);
		Map<String, ?> allEntries = sharePref.getAll();
		for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
			Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
			String cutUri = entry.getKey().toString().substring(0, entry.getKey().toString().length() - 10);
			dataPhotoFavorite.add(getInformationPhotoWithURI(cutUri));

			//Cursor cursor = contentResolver.query()
		}
//		Intent intent = new Intent(this.getContext() , AlbumAdapter.class);
		Bundle bundlePhotoFavourite = new Bundle();
		bundlePhotoFavourite.putParcelableArrayList("photoFavoriteData", dataPhotoFavorite);
		this.setArguments(bundlePhotoFavourite);

		albumsViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
		ArrayList<AlbumModel> addFavouritesAlbum = new ArrayList<AlbumModel>();
		if(!dataPhotoFavorite.isEmpty()) {
			AlbumModel albumFavorite = new AlbumModel() {
				{
					albumName = "Favourites";
					quantity = dataPhotoFavorite.size();
					imageUrl = dataPhotoFavorite.get(dataPhotoFavorite.size() - 1);
				}
			};
			addFavouritesAlbum = albumsViewModel.getAlbumModels();
			addFavouritesAlbum.add(albumFavorite);
		}
		albumAdapter = new AlbumAdapter(this.getContext(),dataPhotoFavorite.isEmpty()?albumsViewModel.getAlbumModels():addFavouritesAlbum, dataPhotoFavorite);
		//System.out.println(albumsViewModel.getAlbumModels());
		mRecyclerView.setAdapter(albumAdapter);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save currently selected layout manager.
		savedInstanceState.putSerializable("layoutManager", mCurrentLayoutManagerType);
		super.onSaveInstanceState(savedInstanceState);
	}
	public PhotoModel getInformationPhotoWithURI(String strUri){
		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_MODIFIED,
				MediaStore.Images.Media.SIZE
		};
		Uri _uri = Uri.parse(strUri);
		Cursor cursor = this.getContext().getContentResolver().query(_uri, projection, null, null,
				MediaStore.Images.Media.DATE_MODIFIED + " DESC");
		int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
		int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
		int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
		int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);

		cursor.moveToFirst();
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
				//uri = ContentUris.withAppendedId(_uri, id);
				uri = _uri;
				isFavorite = true;
			}
		};
		Log.d("Information Photo Favorite",photoModel + "");
		return photoModel;
	}
}