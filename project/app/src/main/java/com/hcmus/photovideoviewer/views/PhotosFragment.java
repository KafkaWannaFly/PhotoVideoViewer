package com.hcmus.photovideoviewer.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.photovideoviewer.MainActivity;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.PhotosViewAdapter;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;

import java.util.ArrayList;
import java.util.function.Function;

public class PhotosFragment extends Fragment {
	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager layoutManager = null;
//	private MutableLiveData<Integer> liveColumnSpan = null;

	private PhotosFragmentViewModel photosViewModel = null;
	private PhotosViewAdapter photosViewAdapter = null;

	private AppBarViewModel appBarViewModel = MainActivity.appBarViewModel;

	private Function<PhotoModel, Boolean> filterFunc;

	public PhotosFragment() {

	}

	public PhotosFragment(AppBarViewModel appBarViewModel) {
		this.appBarViewModel = appBarViewModel;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		photosViewModel = new PhotosFragmentViewModel(MediaDataRepository.getInstance().getPhotoModels());

		Log.d("ActivityLife", "PhotoFragment created");
	}

	@Override
	public void onStart() {
		super.onStart();

		photosViewModel.getLivePhotoModels().setValue(MediaDataRepository.getInstance().fetchPhotos());
		this.filterFunc = (photoModel) -> {
			return photoModel.isSecret;
		};

//		appBarViewModel.liveSortOrder.setValue(appBarViewModel.liveSortOrder.getValue());
		Log.d("ActivityLife", "PhotoFragment start");
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.photos_fragment, container, false);
		recyclerView = rootView.findViewById(R.id.photo_recycle_view);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		try {
			photosViewModel.getLivePhotoModels().observe(getViewLifecycleOwner(), photoModels -> {
				Log.d("ActivityLife", "PhotoFragment data changed");

				if (filterFunc != null) {
					photoModels.removeIf(photoModel -> filterFunc.apply(photoModel));
				}
				appBarViewModel.liveSortOrder.observe(PhotosFragment.this.getViewLifecycleOwner(), order -> {
					if (order == 0) {
						photoModels.sort((o1, o2) -> o2.dateModified.compareTo(o1.dateModified));
					}
					else if (order == 1) {
						photoModels.sort((o1, o2) -> o1.dateModified.compareTo(o2.dateModified));
					}

					if (photosViewAdapter != null) {
						photosViewAdapter.notifyDataSetChanged();
					}
				});

				photosViewAdapter = new PhotosViewAdapter(recyclerView.getContext(), photoModels);

				recyclerView.setAdapter(photosViewAdapter);
			});

			appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
				layoutManager = new GridLayoutManager(getActivity(), columnSpan);
				recyclerView.setLayoutManager(layoutManager);
			});
		} catch (Exception exception) {
			Log.e("PhotosFragmentException", exception.getMessage());
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * Define what photo to be displayed. If not set, PhotoFragment will show all photos in device
	 *
	 * @param filterFunc A function that tells how to filter photo and will be applied to all photos in the list. Return true if photo is taken, false other wise
	 */
	public void setFilterFunc(Function<PhotoModel, Boolean> filterFunc) {
		this.filterFunc = filterFunc;
	}
}