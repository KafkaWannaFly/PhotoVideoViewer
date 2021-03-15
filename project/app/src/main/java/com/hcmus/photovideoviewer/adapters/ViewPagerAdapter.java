package com.hcmus.photovideoviewer.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.views.AlbumsFragment;
import com.hcmus.photovideoviewer.views.ExploreFragment;
import com.hcmus.photovideoviewer.views.PhotosFragment;
import com.hcmus.photovideoviewer.views.VideosFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
	@StringRes
	public static final int[] TAB_TITLES = new int[] {
			R.string.photos_tab,
			R.string.videos_tab,
			R.string.albums_tab,
			R.string.explore_tab
	};

	public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
		super(fragmentActivity);
	}

	public ViewPagerAdapter(@NonNull Fragment fragment) {
		super(fragment);
	}

	public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
		super(fragmentManager, lifecycle);
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		Fragment fragment;
		if(TAB_TITLES[position] == R.string.photos_tab) {
			fragment = PhotosFragment.newInstance();
		}
		else if (TAB_TITLES[position] == R.string.videos_tab) {
			fragment = VideosFragment.newInstance();
		}
		else if (TAB_TITLES[position] == R.string.albums_tab) {
			fragment = AlbumsFragment.newInstance();
		}
		else if (TAB_TITLES[position] == R.string.explore_tab) {
			fragment = ExploreFragment.newInstance();
		}
		else {
			fragment = PhotosFragment.newInstance();
		}

		return fragment;
	}

	@Override
	public int getItemCount() {
		return TAB_TITLES.length;
	}
}
