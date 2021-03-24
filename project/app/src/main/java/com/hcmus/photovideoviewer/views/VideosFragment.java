package com.hcmus.photovideoviewer.views;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.VideosViewModel;

import java.util.ArrayList;

public class VideosFragment extends Fragment {
	private VideosViewModel videosViewModel = null;

	private RecyclerView recyclerView = null;
	private RecyclerView.LayoutManager layoutManager = null;
	private VideoViewAdapter videoViewAdapter = null;
//	private MutableLiveData<Integer> liveColumnSpan = null;
	private AppBarViewModel appBarViewModel = null;

	public static VideosFragment newInstance() {
		return new VideosFragment();
	}

	public VideosFragment() {

	}

//	public VideosFragment(MutableLiveData<Integer> liveColumnSpan) {
//		this.liveColumnSpan = liveColumnSpan;
//	}

	public VideosFragment(AppBarViewModel appBarViewModel) {
		this.appBarViewModel = appBarViewModel;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		videosViewModel = new VideosViewModel(MediaDataRepository.getInstance().getVideoModels());
	}

	@Override
	public void onStart() {
		super.onStart();

		videosViewModel.getLiveVideoModels().setValue(MediaDataRepository.getInstance().fetchVideos());
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

		videosViewModel.getLiveVideoModels().observe(getViewLifecycleOwner(), new Observer<ArrayList<VideoModel>>() {
			@Override
			public void onChanged(ArrayList<VideoModel> videoModels) {

				appBarViewModel.liveSortOrder.observe(getViewLifecycleOwner(), order -> {
					if (order == 0) {
						videoModels.sort((o1, o2) -> o2.dateModified.compareTo(o1.dateModified));
					}
					else if (order == 1) {
						videoModels.sort((o1, o2) -> o1.dateModified.compareTo(o2.dateModified));
					}

					if (videoViewAdapter != null) {
						videoViewAdapter.notifyDataSetChanged();
					}
				});

				videoViewAdapter = new VideoViewAdapter(recyclerView.getContext(), videoModels);
				recyclerView.setAdapter(videoViewAdapter);
			}
		});

		appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
			layoutManager = new GridLayoutManager(getActivity(), columnSpan);
			recyclerView.setLayoutManager(layoutManager);
		});
	}

}