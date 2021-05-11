package com.hcmus.photovideoviewer.views;

import androidx.lifecycle.Observer;
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
import com.hcmus.photovideoviewer.adapters.PhotosViewAdapter;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AlbumsViewModel;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotoViewViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AlbumsFragment extends Fragment {
	enum LayoutManagerType {
		GRID_LAYOUT_MANAGER,
		LINEAR_LAYOUT_MANAGER
	}
	private RecyclerView mRecyclerView;
	private GridLayoutManager gridLayoutManager;
	protected RecyclerView.LayoutManager mLayoutManager;
	protected LayoutManagerType mCurrentLayoutManagerType;
	//	protected String[] mDataset;
	private AlbumAdapter albumAdapter;
	//private AlbumsViewModel albumsViewModel;
	public static AppBarViewModel appBarViewModel = null;
	private ArrayList<AlbumModel> albumModels = new ArrayList<AlbumModel>();
	private PhotosFragmentViewModel photosViewModel = null;

	public AlbumsFragment(AppBarViewModel appBarViewModel){
		this.appBarViewModel = appBarViewModel;

	}
//	public static AlbumsFragment newInstance() {
//		return new AlbumsFragment();
//	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//albumsViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
		photosViewModel = new PhotosFragmentViewModel(MediaDataRepository.getInstance().getPhotoModels());

//		albumModels = MediaDataRepository.getInstance().fetchAlbums(photoModels);
	}

	@Override
	public void onStart() {
		super.onStart();
		photosViewModel.getLivePhotoModels().setValue(MediaDataRepository.getInstance().fetchPhotos());
//		albumModels = MediaDataRepository.getInstance().fetchAlbums(photoModels);
//		CompletableFuture.supplyAsync(() -> {
//			// ABC
//			return 0;
//		}).thenAccept(u -> {
//
//		});
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.albums_fragment, container, false);
		rootView.setTag("RecyclerViewFragment");
		mRecyclerView = rootView.findViewById(R.id.album_recycle_view);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//set live data
//		appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
//			mLayoutManager = new GridLayoutManager(getActivity(), columnSpan);
//			mRecyclerView.setLayoutManager(mLayoutManager);
//		});

		if (savedInstanceState != null) {
			// Restore saved layout manager type.
			mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
					.getSerializable("layoutManager");
		}
		try{
			photosViewModel.getLivePhotoModels().observe(AlbumsFragment.this, new Observer<ArrayList<PhotoModel>>() {
				@Override
				public void onChanged(ArrayList<PhotoModel> photoModels) {
					Log.d("ActivityLife", "AlbumsFragment data changed");
					albumModels = MediaDataRepository.getInstance().fetchAlbums(photoModels);
					albumAdapter = new AlbumAdapter(getContext(), albumModels);
					mRecyclerView.setAdapter(albumAdapter);
				}
			});

			appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
				mLayoutManager = new GridLayoutManager(getActivity(), columnSpan);
				mRecyclerView.setLayoutManager(mLayoutManager);
			});
		} catch (Exception exception) {
			Log.e("PhotosFragmentException", exception.getMessage());
		}
		//albumAdapter = new AlbumAdapter(this.getContext(),albumModels);
		//mRecyclerView.setAdapter(albumAdapter);
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save currently selected layout manager.
		savedInstanceState.putSerializable("layoutManager", mCurrentLayoutManagerType);
		super.onSaveInstanceState(savedInstanceState);
	}
}