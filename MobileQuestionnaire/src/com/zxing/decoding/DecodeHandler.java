/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.decoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.onion.paper.R;
import com.zxing.activity.CaptureActivity;
import com.zxing.camera.CameraManager;
import com.zxing.camera.PlanarYUVLuminanceSource;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final CaptureActivity activity;
  private final MultiFormatReader multiFormatReader;

  DecodeHandler(CaptureActivity activity, Hashtable<DecodeHintType, Object> hints) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.activity = activity;
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.decode:
        //Log.d(TAG, "Got decode message");
        decode((byte[]) message.obj, message.arg1, message.arg2);
        break;
      case R.id.quit:
        Looper.myLooper().quit();
        break;
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
//	  if(width>720 || height>720){
//		  try{
//			  Bitmap b= BitmapFactory.decodeByteArray(data, 0, data.length);
//			  b=suoxiaoBitmap(b);
//			  width=b.getWidth();
//			  height=b.getHeight();
//			  ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			  b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//			  data = baos.toByteArray();
//		  }catch(Exception e){
//			  e.printStackTrace();
//			  return;
//		  }
//	  }
    long start = System.currentTimeMillis();
    byte[] rotatedData = new byte[data.length];  
    for (int y = 0; y < height; y++) {  
        for (int x = 0; x < width; x++)  
        rotatedData[x * height + height - y - 1] = data[x + y * width];  
    }  
    int tmp = width; // Here we are swapping, that's the difference to #11  
    width = height;  
    height = tmp;  
    data = rotatedData;  
    
    Result rawResult = null;    
    PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    
//    Log.e("scancode","bitmap.width="+bitmap.getWidth() + " bitmap.height="+bitmap.getHeight());
    try {
      rawResult = multiFormatReader.decodeWithState(bitmap);
//    	rawResult = multiFormatReader.decode(bitmap);
    } catch (ReaderException re) {
    } finally {
      multiFormatReader.reset();
    }

    if (rawResult != null) {
      long end = System.currentTimeMillis();
      Log.e(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
      Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
      Bundle bundle = new Bundle();
      bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
      message.setData(bundle);
      message.sendToTarget();
    } else {
      Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
      message.sendToTarget();
    }
  }
  
  private Bitmap suoxiaoBitmap(Bitmap bm){
	  // 获得图片的宽高
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    // 设置想要的大小
	    int newWidth = 320;
	    int newHeight = 320;
	    // 计算缩放比例
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight =scaleWidth;// ((float) newHeight) / height;
	    // 取得想要缩放的matrix参数
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    // 得到新的图片
	    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	    return newbm;
  }
	public static void saveBitmap_JPEG(String bitName,Bitmap bitmap,int quality)throws Exception {
		File f = new File(bitName);
		if (!f.exists())
			f.createNewFile();
		CompressFormat format= Bitmap.CompressFormat.JPEG;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(bitName);
			bitmap.compress(format, quality, stream);
			stream.flush();
			stream.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
}
