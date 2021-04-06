package com.hcmus.photovideoviewer.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PhotoModel implements Parcelable {
	public long id;
	public String displayName;
	public double size; //byte
	public Date dateModified;
	public Uri uri;

	public PhotoModel() {

	}

	protected PhotoModel(Parcel in) {
		id = in.readLong();
		displayName = in.readString();
		size = in.readDouble();
		uri = in.readParcelable(Uri.class.getClassLoader());
		dateModified = new Date(in.readLong());
	}

	public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
		@Override
		public PhotoModel createFromParcel(Parcel in) {
			return new PhotoModel(in);
		}

		@Override
		public PhotoModel[] newArray(int size) {
			return new PhotoModel[size];
		}
	};

	@NotNull
	@Override
	public String toString() {
		return "PhotoModel{" +
				       "id=" + id +
				       ", displayName='" + displayName + '\'' +
				       ", size='" + size + '\'' +
				       ", dateModified='" + dateModified + '\'' +
				       '}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(displayName);
		dest.writeDouble(size);
		dest.writeParcelable(uri, flags);
		dest.writeDouble(dateModified.getTime());
	}
}
