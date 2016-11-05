package com.onion.paper.activity.student;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onion.paper.R;
import com.onion.paper.activity.LoginActivity;
import com.onion.paper.adapter.PagerAdapter;
import com.onion.paper.fragment.StudentExploreFragment;
import com.onion.paper.fragment.StudentGradeFragment;
import com.onion.paper.fragment.StudentMeFragment;
import com.onion.paper.tool.QuestionnaireActivityManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class StudentActivity extends FragmentActivity implements OnPageChangeListener, OnCheckedChangeListener, OnClickListener {

	private ViewPager vp_student_class;
	private PagerAdapter mPagerAdapter;
	private List<Fragment> mFragments;
	private RadioGroup rg_student;
	
	private StudentGradeFragment studentGradeFragment;
	private StudentExploreFragment studentExploreFragment;
	private StudentMeFragment studentMeFragment;
	
	private RelativeLayout rl_search;
	
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
		//overridePendingTransition(R.anim.slide_in_right, anim.fade_out);
		setContentView(R.layout.activity_student);
		
		initPermissin();
		
		initComponent();
		UmengUpdateAgent.update(this);
	}
	
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.CAMERA,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_LOGS
    };
    /**
     * 初始化权限
     */
	private void initPermissin() {
		int permission = StudentActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
        	StudentActivity.this.requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
	}
	
	@Override
	protected void onActivityResult(int arg0, int resultCode, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, resultCode, arg2);
		if (resultCode == 6) {
			studentGradeFragment.refreshGrades();
		}
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		// TODO Auto-generated method stub
		QuestionnaireActivityManager.getInstance().addActivity(this);
		vp_student_class = (ViewPager) findViewById(R.id.vp_student_class);
		mFragments = new ArrayList<>();
		studentGradeFragment = new StudentGradeFragment();
		studentExploreFragment = new StudentExploreFragment();
		studentMeFragment = new StudentMeFragment();
		mFragments.add(studentGradeFragment);
		mFragments.add(studentExploreFragment);
		mFragments.add(studentMeFragment);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mFragments);
		vp_student_class.setAdapter(mPagerAdapter);
		vp_student_class.addOnPageChangeListener(this);
		vp_student_class.setOffscreenPageLimit(3);
		vp_student_class.setCurrentItem(0);
		
		rg_student = (RadioGroup) findViewById(R.id.rg_student);
		rg_student.setOnCheckedChangeListener(this);
		
		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		rl_search.setOnClickListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int index) {
		// TODO Auto-generated method stub
		((RadioButton) rg_student.getChildAt(index)).performClick();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// RadioGroup焦点发生变化
		int currentIndex = -1;
		switch (checkedId) {
		case R.id.rb_student_class:
			currentIndex = 0;
			break;
		case R.id.rb_student_explore:
			currentIndex = 1;
			break;
		case R.id.rb_student_me:
			currentIndex = 2;
			break;

		default:
			break;
		}
		
		if (vp_student_class != null && currentIndex > -1) {
			vp_student_class.setCurrentItem(currentIndex);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_search:
			// 打开搜索界面
			Intent exploreIntent = new Intent(StudentActivity.this, SearchActivity.class);
			startActivityForResult(exploreIntent, 6);
			break;

		default:
			break;
		}
	}
	
	private long lastTimeMillis = 0;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		if (System.currentTimeMillis() - lastTimeMillis > 2000) {
			lastTimeMillis = System.currentTimeMillis();
			Toast.makeText(StudentActivity.this, R.string.msg_press_again, Toast.LENGTH_SHORT).show();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}
