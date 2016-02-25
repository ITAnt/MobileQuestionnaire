package com.onion.paper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.onion.paper.R;
import com.onion.paper.SuperConstants;
import com.onion.paper.tool.QuestionnaireActivityManager;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends Activity implements OnClickListener {

	private Button btn_student_exit;
	private CustomedActionBar cab_setting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		initComponent();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		btn_student_exit = (Button) findViewById(R.id.btn_student_exit);
		btn_student_exit.setOnClickListener(this);
		
		cab_setting = (CustomedActionBar) findViewById(R.id.cab_setting);
		cab_setting.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}
	
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_student_exit:
			logoutDialog();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 退出登录对话框
	 */
	private void logoutDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);
		
		TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_exit_title);
		tv_dialog_desc.setText(R.string.label_exit_desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				// 退出学生，清除token
				BmobUser.logOut(SettingActivity.this);
				Tencent mTencent = Tencent.createInstance(SuperConstants.TENCENT_APP_ID,
						SettingActivity.this);
				mTencent.logout(SettingActivity.this);
				QuestionnaireActivityManager.getInstance().clearActivities();
				startActivity(new Intent(SettingActivity.this, LoginActivity.class));
				SettingActivity.this.finish();
			}
		});
		
		tv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		
	}
}
