package com.hcmus.photovideoviewer.constants;

import com.hcmus.photovideoviewer.models.VideoModel;

public class VideoPreferences {
	static public String VIDEOS = "Videos";
	static public String PARCEL_VIDEO_MODEL = "videoModel";
	static public String FAVORITE = "Favorite";

	public static String favoritePreferenceOf(VideoModel videoModel) {
		return FAVORITE + ":" + videoModel.uri;
	}
}
