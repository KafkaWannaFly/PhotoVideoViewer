package com.hcmus.photovideoviewer.constants;

public class PhotoPreferences {
	public static String PHOTOS = "Photos";
	public static String PRIVATE = "Private";
	public static String FAVORITE = "Favorite";
	public static String LOCATION = "Location";
	public static String PARCEL_PHOTOS = "Parcel_Photos";

	public static String privatePreferenceOf(String name) {
		return PRIVATE + ":" + name;
	}

	public static String favoritePreferenceOf(String name) {
		return FAVORITE + ":" + name;
	}

	public static String locationPreferenceOf(String name) {
		return LOCATION + ":" + name;
	}
}
