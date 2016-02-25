package com.onion.paper.activity.student;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;

import com.google.zxing.WriterException;
import com.onion.paper.R;
import com.onion.paper.tool.FileTool;
import com.onion.paper.tool.QrCodeUtil;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightIconClickListener;
import com.umeng.analytics.MobclickAgent;

public class ErweimaActivity extends Activity {

	private CustomedActionBar cab_erweima;
	private Bitmap bmp;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erweima);
		
		initComponent();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		cab_erweima = (CustomedActionBar) findViewById(R.id.cab_erweima);
		// 返回
		cab_erweima.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		cab_erweima.setOnRightIconClickListener(new OnRightIconClickListener() {
			
			@Override
			public void onRightIconClick() {
				// 分享二维码
				if (bmp != null) {
					File file = FileTool.saveBitmap(bmp, getString(R.string.label_app_name));
					if (file != null && file.exists()) {
						// 分享Excel
						Intent share = new Intent(Intent.ACTION_SEND);   
						share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
						share.setType("*/*");//此处可发送多种文件
						startActivity(Intent.createChooser(share, getString(R.string.label_share_code)));
					} else {
						Toast.makeText(ErweimaActivity.this, R.string.msg_get_code_fail, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		ImageView iv_erweima = (ImageView) findViewById(R.id.iv_erweima);
		
		String objectId = BmobUser.getCurrentUser(this).getObjectId();
		if (!TextUtils.isEmpty(objectId)) {
			//调用
			try {
				bmp = QrCodeUtil.getQrCodeImage(250, 250, objectId);
				if (bmp != null) {
					iv_erweima.setImageBitmap(bmp);
				}
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
	}
}
