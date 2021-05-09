package com.hcmus.photovideoviewer.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class VideoModel implements Parcelable {
	public long id;
	public String displayName;
	public long size; //bytes
	public Date dateModified;
	public long duration; //seconds
	public Uri uri;

	public boolean isFavorite = false;

	public VideoModel() {

	}


	protected VideoModel(Parcel in) {
		id = in.readLong();
		displayName = in.readString();
		size = in.readLong();
		duration = in.readLong();
		uri = in.readParcelable(Uri.class.getClassLoader());
		isFavorite = in.readByte() != 0;
		dateModified = new Date(in.readLong());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(displayName);
		dest.writeLong(size);
		dest.writeLong(duration);
		dest.writeParcelable(uri, flags);
		dest.writeByte((byte) (isFavorite ? 1 : 0));
		dest.writeLong(dateModified.getTime());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
		@Override
		public VideoModel createFromParcel(Parcel in) {
			return new VideoModel(in);
		}

		@Override
		public VideoModel[] newArray(int size) {
			return new VideoModel[size];
		}
	};
}
