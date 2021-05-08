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

	public VideoModel() {

	}

	protected VideoModel(Parcel in) {
		id = in.readLong();
		displayName = in.readString();
		size = in.readLong();
		duration = in.readLong();
		uri = in.readParcelable(Uri.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(displayName);
		dest.writeLong(size);
		dest.writeLong(duration);
		dest.writeParcelable(uri, flags);
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

	@NotNull
	@Override
	public String toString() {
		return "VideoModel{" +
				       "id=" + id +
				       ", displayName='" + displayName + '\'' +
				       ", size=" + size +
				       ", dateModified=" + dateModified +
				       ", duration=" + duration +
				       ", uri=" + uri +
				       '}';
	}
}
