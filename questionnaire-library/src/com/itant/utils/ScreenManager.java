package com.itant.utils;

import java.util.Stack;

import android.app.Activity;

/**
 * 界面管理器
 * Screen Manager
 *
 */
public class ScreenManager {

	private static ScreenManager instance;
	private static Stack<Activity> activityStack;

	private ScreenManager() {
	}

	public static ScreenManager getInstance() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}

	/**
	 * 
	 */
	public void removeActivity() {
		Activity activity = activityStack.lastElement();
		if (activity != null) {
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Activity getCurrentActivity() {
		Activity activity = null;
		if (activityStack != null) {
			if (!activityStack.empty()) {
				activity = activityStack.lastElement();
			}
		}
		return activity;
	}

	/**
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 
	 */
	public void removeAllActivity() {
		while (true) {
			Activity activity = getCurrentActivity();
			if (activity == null) {
				break;
			}

			removeActivity(activity);
		}
	}

	/**
	 * @param cls
	 */
	public void removeAllActivityExceptOne(Class<Activity> cls) {
		while (true) {
			Activity activity = getCurrentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			removeActivity(activity);
		}
	}
}