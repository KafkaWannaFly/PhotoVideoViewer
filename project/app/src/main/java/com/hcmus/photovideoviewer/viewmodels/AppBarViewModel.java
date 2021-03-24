package com.hcmus.photovideoviewer.viewmodels;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.hcmus.photovideoviewer.MainApplication;

public class AppBarViewModel {
	public MutableLiveData<Integer> liveColumnSpan = new MutableLiveData<>();
	public MutableLiveData<Integer> liveSortOrder = new MutableLiveData<>(); // 0/1 - New first/Old first

	public AppBarViewModel() {
		liveColumnSpan.setValue(MainApplication.SPAN_COUNT);
		liveSortOrder.setValue(0);
	}
}
