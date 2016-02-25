package com.onion.paper.activity.monitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.itant.view.pulltorefresh.ILoadingLayout;
import com.itant.view.pulltorefresh.PullToRefreshBase;
import com.itant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.itant.view.pulltorefresh.PullToRefreshListView;
import com.onion.paper.R;
import com.onion.paper.adapter.WebPaperAdapter;
import com.onion.paper.adapter.WebPaperAdapter.OnWebPaperClickListener;
import com.onion.paper.model.web.Paper;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.tool.QuestionnaireActivityManager;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.umeng.analytics.MobclickAgent;

public class MonitorActivity extends Activity {

	private List<Paper> mPapers;
	private WebPaperAdapter mAdapter;
	private Context mContext;
	
	private CustomedActionBar cab_published;
	
	
	private PullToRefreshListView ptrlv_questionnaire_list;

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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);

		initComponent();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 6) {
			findPublishedPapers(BmobUser.getCurrentUser(this).getObjectId());
		}
	}
	
	private void initComponent() {
		mContext = getApplicationContext();
		QuestionnaireActivityManager.getInstance().addActivity(this);
		
		// 点击后退
		cab_published = (CustomedActionBar) findViewById(R.id.cab_published);
		cab_published.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		

		ptrlv_questionnaire_list = (PullToRefreshListView) findViewById(R.id.ptrlv_questionnaire_list);
		ptrlv_questionnaire_list.setMode(Mode.PULL_FROM_START);
		ILoadingLayout startLabels = ptrlv_questionnaire_list.getLoadingLayoutProxy(true, false);
		// 刚下拉时，显示的提示
		startLabels.setPullLabel(getString(R.string.label_pull)); 
		// 刷新时
		startLabels.setRefreshingLabel(getString(R.string.label_loading)); 
		// 下来达到一定距离时，显示的提示
		startLabels.setReleaseLabel(getString(R.string.label_release));
		mPapers = new ArrayList<Paper>();
		mAdapter = new WebPaperAdapter(mContext);
		mAdapter.setPapers(mPapers);
		mAdapter.setOnWebPaperClickListener(new OnWebPaperClickListener() {
			
			@Override
			public void onWebPaperClick(Paper paper) {
				// TODO Auto-generated method stub
				// 打开问卷操作界面
				Intent paperDetailIntent = new Intent(MonitorActivity.this, OperateQuestionnairActivity.class);
				paperDetailIntent.putExtra("paper", paper);
				startActivityForResult(paperDetailIntent, 6);
			}
		});

		ptrlv_questionnaire_list.setAdapter(mAdapter);
		ptrlv_questionnaire_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (HttpTool.isNetworkConnected(MonitorActivity.this)) {
					findPublishedPapers(BmobUser.getCurrentUser(MonitorActivity.this).getObjectId());
				} else {
					Toast.makeText(MonitorActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});
		
		findPublishedPapers(BmobUser.getCurrentUser(this).getObjectId());
		
	}

	/**
	 * 根据当前用户的Id查找已经发布的问卷
	 */
	private void findPublishedPapers(String objectId) {
		BmobQuery<Paper> query = new BmobQuery<>();
		query.addWhereEqualTo("userId", objectId);
		query.findObjects(this, new FindListener<Paper>() {
			
			@Override
			public void onSuccess(List<Paper> papers) {
				// TODO Auto-generated method stub
				if (mPapers != null && mPapers.size() > 0) {
					mPapers.clear();
				}
				
				if (papers != null && papers.size() > 0) {
					mPapers.addAll(papers);
				}
				mAdapter.notifyDataSetChanged();
				ptrlv_questionnaire_list.onRefreshComplete();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ptrlv_questionnaire_list.onRefreshComplete();
			}
		});
	}
}
