package com.onion.paper.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.onion.paper.SuperConstants;
import com.onion.paper.model.ExcelFile;

public class FileTool {

	/**
	 * 查找本地Excel文件
	 * 
	 * @return
	 */
	public static List<ExcelFile> findLocalExcels(Context context) {
		List<ExcelFile> excels = null;

		File fileDir = new File(SuperConstants.EXCEL_DIRECTORY);
		if (fileDir.exists()) {
			excels = new ArrayList<ExcelFile>();
			File[] files = fileDir.listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".xls")) {
					// 是Excel文件
					ExcelFile excelFile = new ExcelFile();
					excelFile.setExcelName(file.getName());
					excelFile.setPath(file.getAbsolutePath());
					excelFile.setCreateTime(DateTool.getDefaultName(file
							.lastModified()));
					excels.add(excelFile);
				}
			}
		} else {
			fileDir.mkdirs();
		}
		return excels;
	}

	/**
	 * 清除缓存
	 * 
	 * @param targetFile
	 *            目标文件夹
	 * @return
	 */
	public static void clearCache(File targetFile) {
		File[] files = targetFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					clearCache(file);
				} else {
					file.delete();
				}
			}
		}
	}

	/**
	 * 保存bitmap为文件
	 * 
	 * @param bitmap
	 * @param fileName
	 * @return
	 */
	public static File saveBitmap(Bitmap bitmap, String fileName) {
		File directory = new File(SuperConstants.ERWEIMA_DIRECTORY);
		if (!directory.exists()) {
			boolean createSuc = false;
			createSuc = directory.mkdirs();
			if (!createSuc) {
				return null;
			}
		}

		File file = new File(SuperConstants.ERWEIMA_DIRECTORY + fileName + ".png");
		if (file.exists()) {
			boolean delSuc = file.delete();
			if (!delSuc) {
				return null;
			}
		}

		try {

			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
}
