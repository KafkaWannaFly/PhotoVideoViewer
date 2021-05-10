package com.hcmus.photovideoviewer.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaFileServices;
import com.hcmus.photovideoviewer.viewmodels.PhotoViewViewModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhotoViewActivity extends AppCompatActivity {
	final int DELETE_REQUEST_CODE = 0;
	final int PICK_FOLDER_REQUEST_CODE = 1;
	private final View.OnClickListener copyTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "copyText click!");
		try {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
			startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	};
//	ArrayList<PhotoModel> photoModels = null;
	PhotoModel photoModel = null;
	PhotoViewViewModel photoViewViewModel = null;
//	Integer currentPosition = null;

	private final View.OnClickListener shareTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "shareText click!");

		try {
//			PhotoModel photoModel = photoModels.get(currentPosition);

			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(photoModel.uri));
			shareIntent.setType("image/*");
			startActivity(Intent.createChooser(shareIntent, getString(R.string.send_image)));

		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}
	};
	private final View.OnClickListener favoriteTextClickListener = v -> {
		try {
//			PhotoModel photoModel = photoModel;

			photoViewViewModel.setFavorite(!photoModel.isFavorite);

			Log.d("PhotoViewTextClick", "favoriteText click!");
		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}

	};
	private final View.OnClickListener setPrivateTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "setPrivateText click!");
		try {
//			PhotoModel photoModel = photoModel;

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
//			PhotoModel photoModel = photoModel;

//			photoViewViewModel.deleteImage(photoModel, DELETE_REQUEST_CODE);

			new AlertDialog.Builder(this)
					.setMessage(R.string.ask_sure_delete)
					.setPositiveButton(R.string.yes_im_sure, (dialog, which) -> {
						MediaFileServices.delete(this, photoModel.uri);
						this.finish();
					})
					.setNegativeButton(R.string.cancel, null)
					.show();

		} catch (Exception exception) {
			Log.e("PhotoViewTextClick", exception.getMessage());
		}
	};
	private final View.OnClickListener setBackgroundTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "setBackgroundText click!");
		try {
//			PhotoModel photoModel = photoModel;

			photoViewViewModel.setImageAsBackground(photoModel);

			Toast.makeText(this, getString(R.string.set_background_success), Toast.LENGTH_SHORT).show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	};
	private final View.OnClickListener setLocationTextClickListener = v -> {
		Log.d("PhotoViewTextClick", "setLocationText click!");
		try {
//			PhotoModel photoModel = photoModel;

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(input);

			builder.setPositiveButton(R.string.yes_im_sure, (dialog, which) -> {
				String location = input.getText().toString();

				photoViewViewModel.saveImageLocationPreference(photoModel, location);
				
				photoModel.location = location;

				photoViewViewModel.getLivePhotoModel().setValue(photoModel);

			})
					.setNegativeButton(R.string.cancel, null)
					.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	};

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
		setBackgroundText.setOnClickListener(this.setBackgroundTextClickListener);
		copyText.setOnClickListener(this.copyTextClickListener);
		setLocationText.setOnClickListener(this.setLocationTextClickListener);

		// Get data pass from PhotosFragment
		Intent intent = getIntent();
//		photoModels = intent.getParcelableArrayListExtra("photoModels");
//		currentPosition = intent.getIntExtra("currentPosition", 0);
		photoModel = intent.getParcelableExtra("photoModel");

		photoViewViewModel = new PhotoViewViewModel(this, photoModel);

		Glide.with(getApplicationContext())
				.load(photoModel.uri)
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

			String sizeStr = this.rawByteToStringSize(photoModel.size);
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
					photoModel.isSecret ? R.attr.colorOnPrimary : defaultTextColor.getDefaultColor());

		});
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

	private void setTextViewDrawableTint(TextView textView, int color) {
		for (Drawable drawable : textView.getCompoundDrawables()) {
			if (drawable != null) {
				drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			switch (requestCode) {
				case DELETE_REQUEST_CODE: {
					if (resultCode == RESULT_OK) {
						this.finish();
					}
					break;
				}
				case PICK_FOLDER_REQUEST_CODE: {
					if (data != null) {
						Uri uri = data.getData();
						Log.d("PhotoViewTextClick", "Selected folder: " + uri);

						DocumentFile chosenFolder = DocumentFile.fromTreeUri(this, uri);

						assert chosenFolder != null;
						Uri docUri = DocumentsContract.buildDocumentUriUsingTree(chosenFolder.getUri(),
								DocumentsContract.getTreeDocumentId(chosenFolder.getUri()));
						String path = photoViewViewModel.getPath(this, docUri);

						// This seem pointless but we're able to immediately updated with newly copied
						photoViewViewModel.insertImage(
								photoModel.uri, path + '/' + photoModel.displayName, photoModel);

						Toast.makeText(this, getString(R.string.copy_image_success), Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}
}