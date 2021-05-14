package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditActivity extends AppCompatActivity {
	PhotoModel photoModel;

	PhotoEditorView photoEditorView;
	PhotoEditor photoEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_edit);

		try {
			Intent intent = getIntent();
			photoModel = intent.getParcelableExtra(PhotoPreferences.PHOTO_MODEL);
			if (photoModel == null) {
				finish();
			}

			photoEditorView = findViewById(R.id.photoEditorView);

			ImageView imageView = photoEditorView.getSource();
//			Glide.with(getApplicationContext())
//					.load(photoModel.uri)
//					.placeholder(R.drawable.pussy_cat)
//					.into(imageView);
			imageView.setImageURI(Uri.parse(photoModel.uri));

//		Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

//			Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

			photoEditor = new PhotoEditor.Builder(this, photoEditorView)
					              .setPinchTextScalable(true)
//				               .setDefaultTextTypeface(mTextRobotoTf)
//					              .setDefaultEmojiTypeface(mEmojiTypeFace)
					              .build();
			photoEditor.setBrushDrawingMode(true);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}