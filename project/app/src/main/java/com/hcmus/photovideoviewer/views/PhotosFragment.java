package com.hcmus.photovideoviewer.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.photovideoviewer.MainApplication;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.PhotoViewAdapter;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.PhotosViewModel;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PhotosFragment extends Fragment {
	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager layoutManager = null;

	private PhotosViewModel photosViewModel = null;
	private PhotoViewAdapter photoViewAdapter = null;

	private Function<PhotoModel, Boolean> filterFunc;

	public static PhotosFragment newInstance() {
		return new PhotosFragment();
	}

	public PhotosFragment() {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		photosViewModel = new PhotosViewModel(MediaDataRepository.getInstance().getPhotoModels());

		Log.d("ActivityLife", "PhotoFragment created");
	}

	@Override
	public void onStart() {
		super.onStart();

		photosViewModel.getLivePhotoModels().setValue(MediaDataRepository.getInstance().fetchPhotos());

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

		photosViewModel.getLivePhotoModels().observe(getViewLifecycleOwner(), new Observer<ArrayList<PhotoModel>>() {
			@Override
			public void onChanged(ArrayList<PhotoModel> photoModels) {
				Log.d("ActivityLife", "PhotoFragment data changed");

				if(filterFunc != null) {
					photoModels.removeIf(photoModel -> filterFunc.apply(photoModel));
				}

				photoViewAdapter = new PhotoViewAdapter(recyclerView.getContext(), photoModels);
				recyclerView.setAdapter(photoViewAdapter);

				layoutManager = new GridLayoutManager(getActivity(), MainApplication.SPAN_COUNT);
				recyclerView.setLayoutManager(layoutManager);
			}
		});

	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * Define what photo to be displayed. If not set, PhotoFragment will show all photos in device
	 * @param filterFunc A function that tells how to filter photo and will be applied to all photos in the list. Return true if photo is taken, false other wise
	 */
	public void setFilterFunc(Function<PhotoModel, Boolean> filterFunc) {
		this.filterFunc = filterFunc;
	}
}