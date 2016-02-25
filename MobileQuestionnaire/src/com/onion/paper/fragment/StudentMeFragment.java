package com.onion.paper.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.onion.paper.R;
import com.onion.paper.activity.AboutActivity;
import com.onion.paper.activity.SettingActivity;
import com.onion.paper.activity.student.ErweimaActivity;
import com.onion.paper.model.web.User;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.view.CircleImage;
import com.umeng.analytics.MobclickAgent;

/**
 * 学生界面的个人中心
 * @author 詹子聪
 *
 */
public class StudentMeFragment extends Fragment {
	
	private Activity mActivity;
	private StudentCenterClickListener mListener;
	
	private LinearLayout ll_erweima;
	private LinearLayout ll_setting;
	private LinearLayout ll_about;
	
	private TextView tv_student_name;
	private CircleImage ci_head_icon;
	
	private InfoReceiver mReceiver;
	private boolean hasLoadInfo;
	
	private class InfoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!hasLoadInfo) {
				if (HttpTool.isNetworkConnected(mActivity.getApplicationContext())) {
					loadUserInfo();
				}
			}
		}
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MeScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MeScreen"); 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_student_me, container, false);
		initComponent(view);
		return view;
	}
	
	private void initComponent(View view) {
		mActivity = getActivity();
		mListener = new StudentCenterClickListener();
		
		tv_student_name = (TextView) view.findViewById(R.id.tv_student_name);
		tv_student_name.setText(BmobUser.getCurrentUser(mActivity).getUsername());
		
		ll_erweima = (LinearLayout) view.findViewById(R.id.ll_erweima);
		ll_erweima.setOnClickListener(mListener);
		
		ll_about = (LinearLayout) view.findViewById(R.id.ll_about);
		ll_about.setOnClickListener(mListener);
		
		ll_setting = (LinearLayout) view.findViewById(R.id.ll_setting);
		ll_setting.setOnClickListener(mListener);
		
		ci_head_icon = (CircleImage) view.findViewById(R.id.ci_head_icon);
		ci_head_icon.setOnClickListener(mListener);
		
		loadUserInfo();
		mReceiver = new InfoReceiver();
		IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		mActivity.registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mActivity.unregisterReceiver(mReceiver);
	}
	
	private void loadUserInfo() {
		// TODO Auto-generated method stub
		BmobUser bmobUser = BmobUser.getCurrentUser(mActivity);
		if (bmobUser != null) {
			hasLoadInfo = true;
			BmobQuery<User> user = new BmobQuery<User>();
			user.getObject(mActivity, bmobUser.getObjectId(), new GetListener<User>() {
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onSuccess(User user) {
					tv_student_name.setText(user.getNickName());
					
					BitmapUtils bitmapUtils = new BitmapUtils(mActivity);
					bitmapUtils.display(ci_head_icon, user.getImageUrl(), new BitmapLoadCallBack<View>() {

						@Override
						public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
							ci_head_icon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cat));
						}

						@Override
						public void onLoadCompleted(View arg0, String arg1, Bitmap bitmap, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
							// TODO Auto-generated method stub
							ci_head_icon.setImageBitmap(bitmap);
						}
					});
					user.getImageUrl();
				}
			});
		}
	}
	
	/**
	 * 监听点击事件
	 * @author 詹子聪
	 *
	 */
	private class StudentCenterClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.ll_erweima:
				startActivity(new Intent(mActivity, ErweimaActivity.class));
				break;
			case R.id.ll_setting:
				startActivity(new Intent(mActivity, SettingActivity.class));
				break;
			case R.id.ll_about:
				startActivity(new Intent(mActivity, AboutActivity.class));
				break;
			case R.id.ci_head_icon:
				Toast.makeText(mActivity, R.string.msg_coming, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	}
}
