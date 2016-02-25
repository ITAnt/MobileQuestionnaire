package com.onion.paper.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTool {

	private static final String FORMAT_FILE_NAME = "yyyy/MM/dd HH:mm:ss";
	
	/**
	 * 获取默认文件名
	 * @return
	 */
	public static String getDefaultName(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_FILE_NAME, Locale.getDefault());
		return sdf.format(new Date(time));
	}
	
}
