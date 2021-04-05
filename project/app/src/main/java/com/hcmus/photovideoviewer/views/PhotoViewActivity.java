package com.hcmus.photovideoviewer.views;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcmus.photovideoviewer.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhotoViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_photo_view);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}

		try {
			this.bottomSheetSetup();
		} catch (Exception e) {
			Log.d("BottomSheet", e.getMessage());
		}
	}

	private void bottomSheetSetup() {
		LinearLayout wrapper = findViewById(R.id.bottomSheetLayout);
		BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(wrapper);

		View bottomSheetExpander = findViewById(R.id.bottomSheetExpander);
		bottomSheetExpander.setOnClickListener(v -> {
			if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
			else  {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
	}
}