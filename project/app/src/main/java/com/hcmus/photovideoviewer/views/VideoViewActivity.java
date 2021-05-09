package com.hcmus.photovideoviewer.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.VideoPreferences;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.viewmodels.VideoViewViewModel;

import java.util.concurrent.TimeUnit;

public class VideoViewActivity extends AppCompatActivity {
	TextView videoNameText, sizeText, timeText, locationText, durationText, pathText,
			favoriteText, editText, slideShowText, setBackgroundText,
			setPrivateText, setLocationText,
			shareText, copyText, deleteText;
	ColorStateList defaultTextColor = null;

	private VideoViewViewModel videoViewViewModel;
	private VideoModel videoModel = null;
	private VideoView videoView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_view);

		videoNameText = findViewById(R.id.videoNameText);
		sizeText = findViewById(R.id.sizeText);
		timeText = findViewById(R.id.timeText);
		durationText = findViewById(R.id.durationText);
		pathText = findViewById(R.id.pathText);

		favoriteText = findViewById(R.id.favoriteText);
		copyText = findViewById(R.id.copyText);
		deleteText = findViewById(R.id.deleteText);

		defaultTextColor = videoNameText.getTextColors();

		// CLICK LISTENERS
		favoriteText.setOnClickListener(this.favoriteTextListener());

		Intent intent = getIntent();
		videoModel = intent.getParcelableExtra(VideoPreferences.PARCEL_VIDEO_MODEL);

		videoViewViewModel = new VideoViewViewModel(this, videoModel);

		videoView = findViewById(R.id.videoView);
		videoView.setVideoURI(videoModel.uri);

		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);

		videoView.setMediaController(mediaController);
		videoView.start();

		try {
			this.bottomSheetSetup();
		} catch (Exception e) {
			Log.d("BottomSheet", e.getMessage());
		}
	}

	private View.OnClickListener favoriteTextListener() {
		return v -> {
			try {
				videoViewViewModel.setFavorite(!videoModel.isFavorite);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		};

	}

	private void bottomSheetSetup() {
		LinearLayout wrapper = findViewById(R.id.bottomSheetLayout);
		BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(wrapper);

		View bottomSheetExpander = findViewById(R.id.bottomSheetExpander);
		bottomSheetExpander.setOnClickListener(v -> {
			if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
			else {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		try {
			this.bindBottomSheetToViewModel();
		} catch (Exception exception) {
			Log.e(this.getLocalClassName() + "onWindowFocusChanged", exception.getMessage());
		}
	}

	private void bindBottomSheetToViewModel() {
		videoViewViewModel.getLiveVideoModel().observe(this, liveVideoModel -> {
			videoNameText.setText(liveVideoModel.displayName);

			pathText.setText(liveVideoModel.uri.toString());

			timeText.setText(liveVideoModel.dateModified.toString());

			sizeText.setText(this.rawByteToStringSize(liveVideoModel.size));

			durationText.setText(this.rawSecondToStringTime(videoModel.duration));

			int color = defaultTextColor.getDefaultColor();
			if (liveVideoModel.isFavorite) {
				color = getColor(R.color.favorite_red);
			}
			this.setTextViewDrawableTint(favoriteText, color);
		});
	}

	private void setTextViewDrawableTint(TextView favoriteText, int color) {
		for (Drawable drawable : favoriteText.getCompoundDrawables()) {
			if (drawable != null) {
				drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			}
		}
	}

	private String rawByteToStringSize(double bytes) {
		String postfix = "B";

		if (bytes > 1024) {
			postfix = "KB";
			bytes = bytes / 1024;
		}
		if (bytes > 1024) {
			postfix = "MB";
			bytes = bytes / 1024;
		}
		if (bytes > 1024) {
			postfix = "GB";
			bytes = bytes / 1024;
		}
		bytes = Math.round(bytes * 100D) / 100D;

		return bytes + " " + postfix;
	}

	@SuppressLint("DefaultLocale")
	private String rawSecondToStringTime(long seconds) {
		long second = seconds;
		long minutes = 0;
		long hours = 0;
		if (second > 60) {
			minutes = second / 60;
			second = second % 60;
		}
		if (minutes > 60) {
			hours = minutes / 60;
			minutes = minutes % 60;
		}

		String hourStr = hours < 10 ? "0" + hours : String.valueOf(hours);
		String minuteStr = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
		String secondStr = second < 10 ? "0" + second : String.valueOf(second);

		return hourStr + ":" + minuteStr + ":" + secondStr;
	}

}