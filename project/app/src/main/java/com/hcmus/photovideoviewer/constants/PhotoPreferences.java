package com.hcmus.photovideoviewer.constants;

public class PhotoPreferences {
	public static String PHOTOS = "Photos";
	public static String PRIVATE = "Private";
	public static String FAVORITE = "Favorite";

	public static String privatePreferenceOf(String name) {
		return PRIVATE + ":" + name;
	}

	public static String favoritePreferenceOf(String name) {
		return FAVORITE + ":" + name;
	}
}
