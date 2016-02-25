package com.onion.paper.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import android.content.Context;
import android.util.Log;

import com.onion.paper.SuperConstants;

public class ExcelTool {

	public static boolean saveExcelFile(Context context, String fileName, String[][] contents) { 
 
        boolean success = false; 
 
        //New Workbook
        Workbook wb = new HSSFWorkbook();
 
        Cell c = null;
 
        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        
        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("data");
 
        
        
        for (int i = 0, j = contents.length; i < j; i++) {
        	// Generate column headings
            Row row = sheet1.createRow(i);
        	for (int m = 0, n = contents[0].length; m < n; m++) {
        		c = row.createCell(m);
    	        c.setCellValue(contents[i][m]);
    	        c.setCellStyle(cs);
    	        sheet1.setColumnWidth(m, (15 * 500));
        	}
        }
 
        // Create a path where we will place our List of objects on external storage 
        File file = new File(SuperConstants.EXCEL_DIRECTORY + fileName +".xls"); 
        FileOutputStream os = null; 
 
        try { 
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file); 
            success = true; 
        } catch (IOException e) { 
            Log.w("FileUtils", "Error writing " + file, e); 
        } catch (Exception e) { 
            Log.w("FileUtils", "Failed to save file", e); 
        } finally { 
            try { 
                if (null != os) 
                    os.close(); 
            } catch (Exception ex) { 
            } 
        } 
        return success; 
    }
}
