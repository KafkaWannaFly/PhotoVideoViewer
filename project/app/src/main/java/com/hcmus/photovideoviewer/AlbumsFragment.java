package com.hcmus.photovideoviewer;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcmus.photovideoviewer.models.Albums;
import com.hcmus.photovideoviewer.ui.main.AlbumAdapter;

import java.util.List;

public class AlbumsFragment extends Fragment {
	private enum LayoutManagerType {
		GRID_LAYOUT_MANAGER,
		LINEAR_LAYOUT_MANAGER
	}
	private AlbumsViewModel mViewModel;
	private RecyclerView mRecyclerView;
	private GridLayoutManager gridLayoutManager;
	private List<Albums> albumsList;
	protected RecyclerView.LayoutManager mLayoutManager;
	protected LayoutManagerType mCurrentLayoutManagerType;
	protected AlbumAdapter mAdapter;
	protected String[] mDataset;



	public static AlbumsFragment newInstance() {
		return new AlbumsFragment();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize dataset, this data would usually come from a local content provider or
		// remote server.
		initDataset();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
//		recyclerView = recyclerView.findViewById(R.id.glide_image_list_recycle_view);
//		gridLayoutManager = new GridLayoutManager(this.getContext(), 3, GridLayoutManager.VERTICAL, false);
//		recyclerView.setLayoutManager(gridLayoutManager);
//		recyclerView.setHasFixedSize(true);
//		getAlbumData();
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

		mAdapter = new AlbumAdapter(mDataset);
		// Set CustomAdapter as the adapter for RecyclerView.
		mRecyclerView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(AlbumsViewModel.class);
		// TODO: Use the ViewModel
	}
//	private void getAlbumData()
//	{
//		albumsList = new ArrayList<>();
//		albumsList = AlbumList.setAlbumsData();
//		albumAdapter = new AlbumAdapter(this.getContext(), albumsList);
//		recyclerView.setAdapter(albumAdapter);
//	}
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
	private void initDataset() {
		mDataset = new String[60];
		for (int i = 0; i < 60; i++) {
			mDataset[i] = "This is element #" + i;
		}
	}
}