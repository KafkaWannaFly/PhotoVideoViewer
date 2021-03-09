package com.hcmus.photovideoviewer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
	@SuppressLint("StaticFieldLeak")
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MainApplication.context = getApplicationContext();
	}

	public static Context getContext() {
		return context;
	}
}
