package com.onion.paper.tool;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

public class SystemTool {

	/**
	 * 判断SD卡是否可用
	 * @return true 可用 false 不可用
	 */
	public static boolean isSDCardEnable() {
		if (Environment.getExternalStorageDirectory().exists() &&
				TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 返回本地报文目录
	 */
	public static File getLocalPacketsDirectory() {
		return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/packets/local/");
	}
	
	/**
	 * @return SD卡剩余容量
	 */
	public static long getSDFreeSize(){
	    //取得SD卡文件路径
	    File path = Environment.getExternalStorageDirectory(); 
	    StatFs sf = new StatFs(path.getPath()); 
	    //获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize(); 
	    //空闲的数据块的数量
	    long freeBlocks = sf.getAvailableBlocks();
	    //返回SD卡空闲大小(MB)
	    return (freeBlocks * blockSize) >> 20;
    }	
}
