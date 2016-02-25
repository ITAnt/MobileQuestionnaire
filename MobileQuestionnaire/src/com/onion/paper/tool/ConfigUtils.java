package com.onion.paper.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtils {
	private static ConfigUtils mConfigUtils = null;
	private SharedPreferences mPreferences = null;
	
	private ConfigUtils(Context context) {
		mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}
	
	private static synchronized void initInstance(Context context) {
		if (mConfigUtils == null) {
			mConfigUtils = new ConfigUtils(context);
		}
	}
	
	public static ConfigUtils getConfigUtils(Context context) {
		if (mConfigUtils == null) {
			initInstance(context);
		}
		
		return mConfigUtils;
	}
}
