package com.hcmus.photovideoviewer.views;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcmus.photovideoviewer.MainActivity;
import com.hcmus.photovideoviewer.MainApplication;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.VideoViewAdapter;
import com.hcmus.photovideoviewer.viewmodels.VideosViewModel;

public class VideosFragment extends Fragment {
	private VideosViewModel videosViewModel = null;

	private RecyclerView recyclerView = null;
	private RecyclerView.LayoutManager layoutManager = null;
	private VideoViewAdapter videoViewAdapter = null;

	public static VideosFragment newInstance() {
		return new VideosFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.videos_fragment, container, false);
		recyclerView = rootView.findViewById(R.id.videoRecyclerView);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

		videoViewAdapter = new VideoViewAdapter(recyclerView.getContext(), videosViewModel.getVideoModels());
		recyclerView.setAdapter(videoViewAdapter);

		layoutManager = new GridLayoutManager(getActivity(), MainApplication.SPAN_COUNT);
		recyclerView.setLayoutManager(layoutManager);
	}

}