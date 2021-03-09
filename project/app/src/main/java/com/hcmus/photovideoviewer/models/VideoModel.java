package com.hcmus.photovideoviewer.models;

import java.util.Date;

public class VideoModel {
	public long id;
	public String displayName;
	public long size; //bytes
	public Date dateModified;
	public long duration; //seconds

	@Override
	public String toString() {
		return "VideoModel{" +
				       "id=" + id +
				       ", displayName='" + displayName + '\'' +
				       ", size=" + size +
				       ", dateModified=" + dateModified +
				       ", duration=" + duration +
				       '}';
	}
}
