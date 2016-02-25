package com.onion.paper.tool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;


public class QuestionnaireActivityManager {
	private static QuestionnaireActivityManager mManager = null;
	private static List<Activity> mActivities;
	
	private QuestionnaireActivityManager() {
		mActivities = new ArrayList<>();
	}
	
	private static synchronized void initInstance() {
		if (mManager == null) {
			mManager = new QuestionnaireActivityManager();
		}
	}
	
	public static QuestionnaireActivityManager getInstance() {
		if (mManager == null) {
			initInstance();
		}
		
		return mManager;
	}
	
	public void addActivity(Activity activity) {
		mActivities.add(activity);
	}
	
	public void removeActivity(Activity activity) {
		if (mActivities.contains(activity)) {
			mActivities.remove(activity);
		}
	}
	
	public void clearActivities() {
		if (mActivities != null && mActivities.size() > 0) {
			for (Activity activity : mActivities) {
				if (activity != null) {
					activity.finish();
				}
			}
		}
		mManager = null;
	}
}
