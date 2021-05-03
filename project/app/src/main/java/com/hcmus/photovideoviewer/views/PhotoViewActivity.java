package com.hcmus.photovideoviewer.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.MimeTypeFilter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.viewmodels.PhotoViewViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhotoViewActivity extends AppCompatActivity {
	final int DELETE_REQUEST_CODE = 0;

	ArrayList<PhotoModel> photoModels = null;
	Integer currentPosition = null;
	PhotoViewViewModel photoViewViewModel = null;

	ImageView myPhotoImageView = null;
	TextView photoNameText, sizeText, timeText, locationText, dimensionText, pathText,
			favoriteText, editText, slideShowText, setBackgroundText,
			setPrivateText, setLocationText,
			shareText, copyText, deleteText;
	ColorStateList defaultTextColor = null;

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

		favoriteText = findViewById(R.id.favoriteText);
		editText = findViewById(R.id.editText);
		slideShowText = findViewById(R.id.slideShowText);
		setBackgroundText = findViewById(R.id.setBackgroundText);

		setPrivateText = findViewById(R.id.setPrivateText);
		setLocationText = findViewById(R.id.setLocationText);

		shareText = findViewById(R.id.shareText);
		copyText = findViewById(R.id.copyText);
		deleteText = findViewById(R.id.deleteText);

		defaultTextColor = photoNameText.getTextColors();

		// LISTENERS
		favoriteText.setOnClickListener(this.favoriteTextClickListener);
		setPrivateText.setOnClickListener(this.setPrivateTextClickListener);
		deleteText.setOnClickListener(this.deleteTextClickListener);
		shareText.setOnClickListener(this.shareTextClickListener);

		// Get data pass from PhotosFragment
		Intent intent = getIntent();
		photoModels = intent.getParcelableArrayListExtra("photoModels");
		currentPosition = intent.getIntExtra("currentPosition", 0);

		photoViewViewModel = new PhotoViewViewModel(this, photoModels.get(currentPosition));

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

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		try {
			bindBottomSheetToViewModel();
		} catch (Exception exception) {
			Log.e(this.getLocalClassName() + "onWindowFocusChanged", exception.getMessage());
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
			else {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
	}

	void setPhotoInfo(@NotNull PhotoModel photoModel) {
		this.photoNameText.setText(photoModel.displayName);
		this.locationText.setText(R.string.unknown);
		this.pathText.setText(photoModel.uri.toString());
		this.timeText.setText(photoModel.dateModified.toString());

		double size = photoModel.size;
		String postfix = "B";

		if (size > 1024) {
			postfix = "KB";
			size = size / 1024;
		}
		if (size > 1024) {
			postfix = "MB";
			size = size / 1024;
		}
		if (size > 1024) {
			postfix = "GB";
			size = size / 1024;
		}
		size = Math.round(size * 100) / 100;
		String sizeStr = size + " " + postfix;
		this.sizeText.setText(sizeStr);

		String dimenStr = this.myPhotoImageView.getHeight() + "x" + this.myPhotoImageView.getWidth();
		this.dimensionText.setText(dimenStr);
	}

	@SuppressLint("UseCompatTextViewDrawableApis")
	private void bindBottomSheetToViewModel() {
		photoViewViewModel.getLivePhotoModel().observe(this, photoModel -> {
			photoNameText.setText(photoModel.displayName);

			if (photoModel.location == null) {
				locationText.setText(R.string.unknown);
			}
			else {
				locationText.setText(photoModel.location);
			}

			double size = photoModel.size;
			String postfix = "B";

			if (size > 1024) {
				postfix = "KB";
				size = size / 1024;
			}
			if (size > 1024) {
				postfix = "MB";
				size = size / 1024;
			}
			if (size > 1024) {
				postfix = "GB";
				size = size / 1024;
			}
			size = Math.round(size * 100) / 100;
			String sizeStr = size + " " + postfix;
			this.sizeText.setText(sizeStr);

			String dimenStr = this.myPhotoImageView.getHeight() + "x" + this.myPhotoImageView.getWidth();
			this.dimensionText.setText(dimenStr);

			pathText.setText(photoModel.uri.toString());
			timeText.setText(photoModel.dateModified.toString());

			int color = defaultTextColor.getDefaultColor();
			if (photoModel.isFavorite) {
				color = getColor(R.color.favorite_red);
			}
			this.setTextViewDrawableTint(favoriteText, color);

			setTextViewDrawableTint(setPrivateText,
					photoModel.isSecret? R.attr.colorOnPrimary : defaultTextColor.getDefaultColor());

		});
	}

	private void setTextViewDrawableTint(TextView textView, int color) {
		for (Drawable drawable : textView.getCompoundDrawables()) {
			if(drawable != null) {
				drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			}
		}
	}

	private final View.OnClickListener favoriteTextClickListener = v -> {
		try {
			PhotoModel photoModel = photoModels.get(currentPosition);

			photoViewViewModel.setFavorite(!photoModel.isFavorite);

			Log.d("PhotoViewTextClick", "favoriteText click!");
		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}

	};

	private final View.OnClickListener setPrivateTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "setPrivateText click!");
		try {
			PhotoModel photoModel = photoModels.get(currentPosition);

			String msg;
			if (photoModel.isSecret) {
				msg = getString(R.string.set_private_to_public);
			}
			else {
				msg = getString(R.string.set_public_to_private);
			}

			new AlertDialog.Builder(this)
					.setMessage(msg)
					.setPositiveButton(R.string.yes_im_sure, (dialog, which) -> {
						try {
							photoViewViewModel.setPrivate(!photoModel.isSecret);
							this.finish();
						} catch (IOException e) {
							e.printStackTrace();
						}
					})
					.setNegativeButton(R.string.cancel, null)
					.show();
		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}
	};

	private final View.OnClickListener deleteTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "deleteText click!");

		try {
			PhotoModel photoModel = photoModels.get(currentPosition);

//			photoViewViewModel.deleteImage(photoModel, DELETE_REQUEST_CODE);

			new AlertDialog.Builder(this)
					.setMessage(R.string.ask_sure_delete)
					.setPositiveButton(R.string.yes_im_sure, (dialog, which) -> {
						photoViewViewModel.deletePhotoWithoutAsking(photoModel.uri);
						this.finish();
					})
					.setNegativeButton(R.string.cancel, null)
					.show();

		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}
	};

	private final View.OnClickListener shareTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "shareText click!");

		try {
			PhotoModel photoModel = photoModels.get(currentPosition);

			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(photoModel.uri));
			shareIntent.setType("image/*");
			startActivity(Intent.createChooser(shareIntent, getString(R.string.send_image)));

//			photoViewViewModel.sharePhoto(photoModel);

		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == DELETE_REQUEST_CODE) {
			Log.d("PhotoViewTextClick", "Result code: " + resultCode);
			if (resultCode == RESULT_OK) {
				this.finish();
			}
		}
	}
}