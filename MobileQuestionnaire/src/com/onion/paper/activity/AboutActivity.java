package com.onion.paper.activity;

import android.app.Activity;
import android.os.Bundle;

import com.onion.paper.R;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends Activity {

	private CustomedActionBar cab_about;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initComponent();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		cab_about = (CustomedActionBar) findViewById(R.id.cab_about);
		cab_about.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
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
}
