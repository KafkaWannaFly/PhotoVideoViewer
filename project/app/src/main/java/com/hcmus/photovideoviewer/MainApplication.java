package com.hcmus.photovideoviewer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
	@SuppressLint("StaticFieldLeak")
	private static Context context;
	public static final int SPAN_COUNT = 3; // Number of displayed columns in view

	@Override
	public void onCreate() {
		super.onCreate();
		MainApplication.context = getApplicationContext();
	}

	public static Context getContext() {
		return context;
	}
}
