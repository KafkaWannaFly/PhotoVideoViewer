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
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AlbumsViewModel;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotoViewViewModel;

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
		albumsViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
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
		appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
			mLayoutManager = new GridLayoutManager(getActivity(), columnSpan);
			mRecyclerView.setLayoutManager(mLayoutManager);
		});

		if (savedInstanceState != null) {
			// Restore saved layout manager type.
			mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
					.getSerializable("layoutManager");
		}

		albumAdapter = new AlbumAdapter(this.getContext(),albumsViewModel.getAlbumModels());
		mRecyclerView.setAdapter(albumAdapter);
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save currently selected layout manager.
		savedInstanceState.putSerializable("layoutManager", mCurrentLayoutManagerType);
		super.onSaveInstanceState(savedInstanceState);
	}
}