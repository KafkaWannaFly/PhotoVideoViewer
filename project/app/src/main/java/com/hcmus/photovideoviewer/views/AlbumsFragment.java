package com.hcmus.photovideoviewer.views;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcmus.photovideoviewer.adapters.AlbumAdapter;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.viewmodels.AlbumsViewModel;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosViewModel;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {
	private enum LayoutManagerType {
		GRID_LAYOUT_MANAGER,
		LINEAR_LAYOUT_MANAGER
	}
	private AlbumsViewModel mViewModel;
	private RecyclerView mRecyclerView;
	private GridLayoutManager gridLayoutManager;
	protected RecyclerView.LayoutManager mLayoutManager;
	protected LayoutManagerType mCurrentLayoutManagerType;
	protected AlbumAdapter mAdapter;
	//	protected String[] mDataset;
	private AlbumAdapter albumAdapter;
	private AlbumsViewModel albumsViewModel;


	public static AlbumsFragment newInstance() {
		return new AlbumsFragment();
	}
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
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.glide_image_list_recycle_view);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;


		if (savedInstanceState != null) {
			// Restore saved layout manager type.
			mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
					.getSerializable("layoutManager");
		}
		setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
		albumsViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
		albumAdapter = new AlbumAdapter(albumsViewModel.getAlbumModels());
		mRecyclerView.setAdapter(albumAdapter);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
	}
	public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
		int scrollPosition = 0;

		// If a layout manager has already been set, get current scroll position.
		if (mRecyclerView.getLayoutManager() != null) {
			scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
					.findFirstCompletelyVisibleItemPosition();
		}

		switch (layoutManagerType) {
			case GRID_LAYOUT_MANAGER:
				mLayoutManager = new GridLayoutManager(getActivity(), 2);
				mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
				break;
			case LINEAR_LAYOUT_MANAGER:
				mLayoutManager = new LinearLayoutManager(getActivity());
				mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
				break;
			default:
				mLayoutManager = new LinearLayoutManager(getActivity());
				mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
		}

		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.scrollToPosition(scrollPosition);
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save currently selected layout manager.
		savedInstanceState.putSerializable("layoutManager", mCurrentLayoutManagerType);
		super.onSaveInstanceState(savedInstanceState);
	}

}