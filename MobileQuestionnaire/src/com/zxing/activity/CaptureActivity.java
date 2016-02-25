package com.zxing.activity;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.onion.paper.R;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.view.ViewfinderView;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class CaptureActivity extends Activity implements OnClickListener, Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	// private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	// 扫描完是否播放声音
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	// 扫描完是否震动
	private boolean vibrate;

	private Button btnCancel;
	private CustomedActionBar cab_scan_code;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zxing_activity_scan);
		//setActionBarIcon(R.drawable.back);
		//setActionBarTitle(R.string.label_getting_permission);
		
		setScanWidthHeight();	

		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
		hasSurface = false;
		
		// 返回
		cab_scan_code = (CustomedActionBar) findViewById(R.id.cab_scan_code);
		cab_scan_code.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		// inactivityTimer = new InactivityTimer(this);

	}
	
	private void setScanWidthHeight(){
		//设置扫描的大小
		DisplayMetrics metrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthPixels=metrics.widthPixels;
		int heightPixels=metrics.heightPixels;
		int width=widthPixels<heightPixels?widthPixels:heightPixels;
		if(width<=0)
			width=320;
		CameraManager.MIN_FRAME_WIDTH = (int)(width*3/5); 
		CameraManager.MIN_FRAME_HEIGHT = (int)(width*3/5); 
		CameraManager.MAX_FRAME_WIDTH = (int)(width*3/5);//(int)(width*2/3);    
		CameraManager.MAX_FRAME_HEIGHT = (int)(width*3/5); 		
//		Log.e("distance","CameraManager.MAX_FRAME_WIDTH="+CameraManager.MAX_FRAME_WIDTH+" CameraManager.MAX_FRAME_HEIGHT="+CameraManager.MAX_FRAME_HEIGHT);
		
//		CameraManager.MIN_FRAME_WIDTH = 240; 
//		CameraManager.MIN_FRAME_HEIGHT =240; 
//		CameraManager.MAX_FRAME_WIDTH = 1200;//(int)(width*2/3);    
//		CameraManager.MAX_FRAME_HEIGHT = 675; 	
//		CameraManager.MIN_FRAME_WIDTH = width; 
//		CameraManager.MIN_FRAME_HEIGHT =width; 
//		CameraManager.MAX_FRAME_WIDTH = width;//(int)(width*2/3);    
//		CameraManager.MAX_FRAME_HEIGHT = width; 	
	}

	/**
	 * 画面元素点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		// inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		// inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (TextUtils.isEmpty(resultString)) {
			Toast.makeText(CaptureActivity.this, R.string.label_scan_fail, Toast.LENGTH_SHORT).show();
		} else {
			// 扫描结果=====================================================================
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("objectId", resultString);
			resultIntent.putExtras(bundle);
			this.setResult(6, resultIntent);
		}
		CaptureActivity.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}