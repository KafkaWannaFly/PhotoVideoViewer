package com.hcmus.photovideoviewer.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.views.AlbumsFragment;
//import com.hcmus.photovideoviewer.views.ExploreFragment;
import com.hcmus.photovideoviewer.views.ExploreFragment;
import com.hcmus.photovideoviewer.views.PhotosFragment;
import com.hcmus.photovideoviewer.views.VideosFragment;

public class MainPagerAdapter extends FragmentStateAdapter {
	@StringRes
	public static final int[] TAB_TITLES = new int[] {
			R.string.photos_tab,
			R.string.videos_tab,
			R.string.albums_tab,
			R.string.explore_tab
	};

	AppBarViewModel appBarViewModel;

	public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
		super(fragmentActivity);
	}

	public MainPagerAdapter(@NonNull Fragment fragment) {
		super(fragment);
	}

	public MainPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
		super(fragmentManager, lifecycle);
	}


	public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity, AppBarViewModel appBarViewModel) {
		super(fragmentActivity);

		this.appBarViewModel = appBarViewModel;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		Fragment fragment;
		if(TAB_TITLES[position] == R.string.photos_tab) {
			fragment = new PhotosFragment(appBarViewModel);
		}
		else if (TAB_TITLES[position] == R.string.videos_tab) {
			fragment = new VideosFragment(appBarViewModel);
		}
		else if (TAB_TITLES[position] == R.string.albums_tab) {
//			fragment = AlbumsFragment.newInstance();
			fragment = new AlbumsFragment(appBarViewModel);
		}
		else if (TAB_TITLES[position] == R.string.explore_tab) {
			fragment = new ExploreFragment(appBarViewModel);
		}
		else {
			fragment = new PhotosFragment(appBarViewModel);
		}

		return fragment;
	}

	@Override
	public int getItemCount() {
		return TAB_TITLES.length;
	}
}
