package com.hcmus.photovideoviewer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainApplication extends Application {
	@SuppressLint("StaticFieldLeak")
	private static Context context;
	public static int SPAN_COUNT = 3; // Number of displayed columns in view
	SharedPreferences sharedPreferences;

	@Override
	public void onCreate() {
		super.onCreate();

		//run python API
		if(!Python.isStarted())
			Python.start(new AndroidPlatform(getApplicationContext()));

		MainApplication.context = getApplicationContext();
		sharedPreferences = getSharedPreferences("MySetting", MODE_PRIVATE);

		SPAN_COUNT = sharedPreferences.getInt("spanCount", 3);
	}

	public static Context getContext() {
		return context;
	}
}
