package com.itant.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public class UIUtils {

	/**
	 * 合并数组
	 * 
	 * @param array
	 * @return
	 */
	public static String combineArray(String[] array) {
		if (array == null || array.length == 0) {
			return "";
		}
		String result = "";
		int length = array.length;
		if (length == 1) {
			return array[0];
		} else {
			for (int i = 0; i < length; i++) {
				if (i < length - 1) {
					result += (String) array[i] + ", ";
				} else {
					result += (String) array[i];
				}
			}
		}
		return result;
	}
	
	/**
	 * make file path
	 * 
	 * @param mContext
	 * @param filename
	 * @param forlderName
	 * @return
	 */
	public static String makeFilePath(Context mContext, String forlderName) {
		File cacheDir = new File(getFileDir(mContext), forlderName);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir.getPath();
	}

	/**
	 * 获取文件目录
	 * 
	 * @param mContext
	 * @return
	 */
	public static File getFileDir(Context mContext) {
		File cacheDir = null;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = mContext.getExternalFilesDir(null);
		} else {
			cacheDir = mContext.getFilesDir();

		}
		return cacheDir;
	}

	/**
	 * 创建文件目录
	 * 
	 * @param dir
	 * @return
	 */
	public static File createFileDir(String dir) {
		if (TextUtils.isEmpty(dir)) {
			return null;
		}

		File cacheDir = new File(dir);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	/**
	 * 判断是否是平板
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTablet(Context context) {
		if (null == context) {
			return false;
		}
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * dip转换成px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px转换成dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断当前系统时间是否是24小时制
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean is24Hours(Context mContext) {
		try {
			ContentResolver cv = mContext.getContentResolver();
			String strTimeFormat = android.provider.Settings.System.getString(
					cv, android.provider.Settings.System.TIME_12_24);
			if (strTimeFormat.equals("24")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 获取APP版本名称
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getVersionName(Context mContext) {
		PackageManager packageManager = mContext.getPackageManager();
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(),
					0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	public static boolean isNotEmpty(String param) {
		if (!TextUtils.isEmpty(param) && !"null".equalsIgnoreCase(param)) {
			return true;
		}
		return false;
	}

	/**
	 * 拷贝文件到指定目录
	 * 
	 * @param fromFile
	 * @param toFile
	 */
	public static void copyFile(String fromFile, String toFile) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(fromFile);
			outputStream = new FileOutputStream(toFile);
			byte[] b = new byte[1024];
			int readedLength = -1;
			while ((readedLength = inputStream.read(b)) != -1) {
				outputStream.write(b, 0, readedLength);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 保存图片
	 * 
	 * @param imageUrl
	 * @param mBitmap
	 */
	public static void saveBitmap(String imageUrl, Bitmap mBitmap) {
		if (TextUtils.isEmpty(imageUrl)) {
			return;
		}

		File f = new File(imageUrl);
		try {
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
		} catch (Exception e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
			fOut.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fOut != null) {
					fOut.close();
				}
			} catch (IOException e2) {
			}
		}
	}

	/**
	 * Generate one unique uuid
	 * 
	 * @return uuid string
	 */
	public static String generateUUID() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String uuidString = dateFormat.format(new Date());
		return uuidString;
	}

	/**
	 * to check whether installed APP in local device.
	 * 
	 */
	public static boolean isInstalledAPP(Context context, String appPackageName) {
		if (context == null)
			return false;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					appPackageName, 0);
			return info != null;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param context
	 * @param action
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list != null && list.size() > 0;
	}

	/**
	 * 检查是否支持拨号功能
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSupportCallPhone(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
			return true;
		}
		return false;
	}

	/**
	 * read Picture Degree
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * rotate image 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// rotate
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// create new bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 获取圆角图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		// 保证是方形，并且从中心画
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int w;
		int deltaX = 0;
		int deltaY = 0;
		if (width <= height) {
			w = width;
			deltaY = height - w;
		} else {
			w = height;
			deltaX = width - w;
		}
		final Rect rect = new Rect(deltaX, deltaY, w, w);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// 圆形，所有只用一个

		int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/**
	 * 获取屏幕宽度
	 * @param mActivity
	 * @return
	 */
	public static int getScreenWidth(Activity mActivity) {
		DisplayMetrics metrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}
	
	/**
	 * 获取屏幕高度
	 * @param mActivity
	 * @return
	 */
	public static int getScreenHeight(Activity mActivity) {
		DisplayMetrics metrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}
}
