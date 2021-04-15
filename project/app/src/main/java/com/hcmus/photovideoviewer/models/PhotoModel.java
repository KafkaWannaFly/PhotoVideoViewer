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

	public boolean isFavorite = false;
	public boolean isSecret = false;
	public Uri formerUri = null;

	public String location = null;

	public PhotoModel() {

	}


	protected PhotoModel(Parcel in) {
		id = in.readLong();
		displayName = in.readString();
		size = in.readDouble();
		uri = in.readParcelable(Uri.class.getClassLoader());
		isFavorite = in.readByte() != 0;
		isSecret = in.readByte() != 0;
		formerUri = in.readParcelable(Uri.class.getClassLoader());
		location = in.readString();
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
		dest.writeByte((byte) (isFavorite ? 1 : 0));
		dest.writeByte((byte) (isSecret ? 1 : 0));
		dest.writeParcelable(formerUri, flags);
		dest.writeString(location);
		dest.writeLong(dateModified.getTime());
	}
}
