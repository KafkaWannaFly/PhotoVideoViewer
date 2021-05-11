package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.SlideShowAdapter;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;

import java.util.ArrayList;

public class SlideShowActivity extends AppCompatActivity {
	long DELAY_SLIDE = 2000;

	ViewPager2 viewPager2;
	Handler handler = new Handler();
	ArrayList<PhotoModel> photoModels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_show);

		viewPager2 = findViewById(R.id.slideShowViewPager2);

		Intent intent = getIntent();
		photoModels = intent.getParcelableArrayListExtra(PhotoPreferences.PARCEL_PHOTOS);

		SlideShowAdapter slideShowAdapter = new SlideShowAdapter(this, photoModels);

		viewPager2.setAdapter(slideShowAdapter);

		viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				handler.removeCallbacks(slideRunnable);
				handler.postDelayed(slideRunnable, DELAY_SLIDE);
			}
		});

	}

	private final Runnable slideRunnable = () -> {
		viewPager2.setCurrentItem((viewPager2.getCurrentItem() + 1) % photoModels.size());
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(slideRunnable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		handler.postDelayed(slideRunnable, DELAY_SLIDE);
	}
}