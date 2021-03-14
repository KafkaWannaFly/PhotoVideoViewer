package com.hcmus.photovideoviewer.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hcmus.photovideoviewer.views.AlbumsFragment;
import com.hcmus.photovideoviewer.views.ExploreFragment;
import com.hcmus.photovideoviewer.views.PhotosFragment;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.views.VideosFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	@StringRes
	private static final int[] TAB_TITLES = new int[] {
		R.string.photos_tab,
		R.string.videos_tab,
		R.string.albums_tab,
		R.string.explore_tab
	};

	private final Context mContext;

	public SectionsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;

		switch (TAB_TITLES[position]) {
			case R.string.photos_tab: {
				fragment = PhotosFragment.newInstance();
				break;
			}
			case R.string.videos_tab: {
				fragment = VideosFragment.newInstance();
				break;
			}
			case R.string.albums_tab: {
				fragment = AlbumsFragment.newInstance();
				break;
			}
			case R.string.explore_tab: {
				fragment = ExploreFragment.newInstance();
				break;
			}
			default:
				fragment = PhotosFragment.newInstance();
		}

		return fragment;
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getResources().getString(TAB_TITLES[position]);
	}

	@Override
	public int getCount() {
		return TAB_TITLES.length;
	}
}