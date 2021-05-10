package com.hcmus.photovideoviewer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.PhotoModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SlideShowAdapter extends RecyclerView.Adapter<SlideShowAdapter.ViewHolder> {
	Context context;
	ArrayList<PhotoModel> photoModels;

	public SlideShowAdapter(Context context, ArrayList<PhotoModel> photoModels) {
		this.context = context;
		this.photoModels = photoModels;
	}

	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				         .inflate(R.layout.slide_show_item, parent, false);
		return new SlideShowAdapter.ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		ImageView imageView = holder.getSlideShowImage();

		PhotoModel photoModel = photoModels.get(position);
		Glide.with(context)
				.load(photoModel.uri)
				.placeholder(R.drawable.pussy_cat)
				.into(imageView);
	}

	@Override
	public int getItemCount() {
		return this.photoModels.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView slideShowImage;

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			slideShowImage = itemView.findViewById(R.id.slideShowImage);
		}

		public ImageView getSlideShowImage() {
			return slideShowImage;
		}
	}
}
