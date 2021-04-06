package com.hcmus.photovideoviewer.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.PhotoModel;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhotoViewActivity extends AppCompatActivity {

	ArrayList<PhotoModel> photoModels = null;
	Integer currentPosition = null;

	ImageView myPhotoImageView = null;
	TextView photoNameText, sizeText, timeText, locationText, dimensionText, pathText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_photo_view);

		myPhotoImageView = findViewById(R.id.photo);

		photoNameText = findViewById(R.id.photoNameText);
		sizeText = findViewById(R.id.sizeText);
		timeText = findViewById(R.id.timeText);
		locationText = findViewById(R.id.locationText);
		dimensionText = findViewById(R.id.dimensionText);
		pathText = findViewById(R.id.pathText);

		// Get data pass from PhotosFragment
		Intent intent = getIntent();
		photoModels = intent.getParcelableArrayListExtra("photoModels");
		currentPosition = intent.getIntExtra("currentPosition", 0);

		Glide.with(getApplicationContext())
				.load(photoModels.get(currentPosition).uri)
				.placeholder(R.drawable.pussy_cat)
				.into(myPhotoImageView);

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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		setPhotoInfo(photoModels.get(currentPosition));
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

	void setPhotoInfo(PhotoModel photoModel) {
		this.photoNameText.setText(photoModel.displayName);
		this.locationText.setText("Somewhere on Earth");
		this.pathText.setText(photoModel.uri.toString());
		this.timeText.setText(photoModel.dateModified.toString());

		double size = photoModel.size;
		String postfix = "B";

		if (size > 1024) {
			postfix = "KB";
			size = size / 1024;
		}
		if(size > 1024) {
			postfix = "MB";
			size = size / 1024;
		}
		if(size > 1024) {
			postfix = "GB";
			size = size / 1024;
		}
		size = Math.round(size * 100) / 100;
		String sizeStr = size + " " + postfix;
		this.sizeText.setText(sizeStr);

		String dimenStr = this.myPhotoImageView.getHeight() + "x" + this.myPhotoImageView.getWidth();
		this.dimensionText.setText(dimenStr);
	}
}