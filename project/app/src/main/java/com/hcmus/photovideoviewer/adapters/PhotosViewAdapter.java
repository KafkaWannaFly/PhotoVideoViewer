package com.hcmus.photovideoviewer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.views.PhotoViewActivity;
import com.hcmus.photovideoviewer.views.PhotosFragment;

import java.util.ArrayList;

public class PhotosViewAdapter extends RecyclerView.Adapter<PhotosViewAdapter.ViewHolder> {
	private ArrayList<PhotoModel> photoModels = null;
	Context context = null;

	public PhotosViewAdapter(Context context, ArrayList<PhotoModel> photoModels) {
			this.context = context;
		this.photoModels = photoModels;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				         .inflate(R.layout.photo_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageView imageView = holder.getImageView();

		imageView.setOnClickListener(v -> {
			Intent viewPhotoIntent = new Intent(context, PhotoViewActivity.class);
//			viewPhotoIntent.putParcelableArrayListExtra("photoModels", photoModels);
//			viewPhotoIntent.putExtra("currentPosition", position);
			viewPhotoIntent.putExtra(PhotoPreferences.PHOTO_MODEL, photoModels.get(position));
			context.startActivity(viewPhotoIntent);
		});

		PhotoModel photoModel = photoModels.get(position);
		Glide.with(context)
				.load(photoModel.uri)
				.placeholder(R.drawable.pussy_cat)
				.into(imageView);
	}

	@Override
	public int getItemCount() {
		return photoModels.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final ImageView imageView;
		private final ViewGroup container;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView);
			container = itemView.findViewById(R.id.photoRecycleItemLayout);
		}

		public ImageView getImageView() {
			return imageView;
		}

		public void hideItem() {
			itemView.setVisibility(View.GONE);
			container.setVisibility(View.GONE);
			itemView.setVisibility(View.GONE);

			imageView.setMaxHeight(0);
			imageView.setMaxWidth(0);
		}
	}
}
