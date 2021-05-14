package com.hcmus.photovideoviewer.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaFileServices;
import com.hcmus.photovideoviewer.viewmodels.PhotoEditViewModel;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditActivity extends AppCompatActivity {
	PhotoModel photoModel;

	PhotoEditorView photoEditorView;
	PhotoEditor photoEditor;

	TextView brushText, eraserText, textText, emojiText, statusText;
	ImageView saveText, undoText, redoText;

	ViewGroup colorContainerLayout;

	PhotoEditViewModel photoEditViewModel = new PhotoEditViewModel();

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_edit);

		// Find View
		brushText = findViewById(R.id.brushText);
		eraserText = findViewById(R.id.eraserText);
		textText = findViewById(R.id.textText);
		emojiText = findViewById(R.id.emojiText);
		saveText = findViewById(R.id.saveText);
		undoText = findViewById(R.id.undoText);
		redoText = findViewById(R.id.redoText);
		statusText = findViewById(R.id.statusText);

		colorContainerLayout = findViewById(R.id.colorContainerLayout);

		// Set Listeners
		brushText.setOnClickListener(this.brushTextListener());
		this.setBrushColorListener(colorContainerLayout);
		undoText.setOnClickListener(this.undoTextListener());
		redoText.setOnClickListener(this.redoTextListener());
		eraserText.setOnClickListener(this.eraserTextListener());

		saveText.setOnClickListener(this.saveTextListener());

		try {
			Intent intent = getIntent();
			photoModel = intent.getParcelableExtra(PhotoPreferences.PHOTO_MODEL);
			if (photoModel == null) {
				finish();
			}

			photoEditorView = findViewById(R.id.photoEditorView);

			ImageView imageView = photoEditorView.getSource();
			imageView.setImageURI(Uri.parse(photoModel.uri));


			photoEditor = new PhotoEditor.Builder(this, photoEditorView)
					              .setPinchTextScalable(true)
//				               .setDefaultTextTypeface(mTextRobotoTf)
//					              .setDefaultEmojiTypeface(mEmojiTypeFace)
					              .build();

			photoEditViewModel.getMutableLiveStatus().observe(this, status -> {
				if (status.isEmpty()) {
					statusText.setVisibility(View.GONE);
					colorContainerLayout.setVisibility(View.GONE);
				}
				else {
					statusText.setVisibility(View.VISIBLE);
					statusText.setText(status);

					colorContainerLayout.setVisibility(View.VISIBLE);
				}
			});
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private View.OnClickListener eraserTextListener() {
		return v -> {
			photoEditor.brushEraser();

			photoEditViewModel.getMutableLiveStatus().setValue(getString(R.string.eraser));
		};
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@SuppressLint("MissingPermission")
	private View.OnClickListener saveTextListener() {
		return v -> {
			new AlertDialog.Builder(this)
					.setMessage(R.string.save_edited_photo)
					.setPositiveButton(R.string.yes_im_sure, (dialog, which) -> {
						String newName = photoModel.uri.replace(photoModel.displayName,
								new Date().getTime() + photoModel.displayName);
						photoEditor.saveAsFile(newName,
								new PhotoEditor.OnSaveListener() {

									@Override
									public void onSuccess(@NonNull @NotNull String s) {
										Log.d("PhotoEdit", "Save edited photo at: " + s);
										MediaFileServices.refreshMediaStore(PhotoEditActivity.this);
										PhotoEditActivity.this.finish();
									}

									@Override
									public void onFailure(@NonNull @NotNull Exception e) {
										e.printStackTrace();
									}
								});
					})
					.setNegativeButton(R.string.cancel, null)
					.show();

		};
	}

	private View.OnClickListener redoTextListener() {
		return v -> {
			photoEditor.redo();
		};
	}

	private View.OnClickListener undoTextListener() {
		return v -> {
			photoEditor.undo();
		};
	}

	private View.OnClickListener brushTextListener() {
		return v -> {
			photoEditor.setBrushDrawingMode(!photoEditor.getBrushDrawableMode());

			photoEditViewModel.getMutableLiveStatus().setValue(photoEditor.getBrushDrawableMode() ?
					                                                   getString(R.string.brush) : "");
		};
	}

	private void setBrushColorListener(ViewGroup colorContainerLayout) {
		for (int i = 0; i < colorContainerLayout.getChildCount(); i++) {
			View view = colorContainerLayout.getChildAt(i);

			view.setOnClickListener(v -> {
				ColorDrawable colorDrawable = (ColorDrawable) v.getBackground();
				photoEditor.setBrushColor(colorDrawable.getColor());
			});

		}
	}
}