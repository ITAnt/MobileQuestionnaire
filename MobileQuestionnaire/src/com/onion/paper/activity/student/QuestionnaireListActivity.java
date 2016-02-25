package com.onion.paper.activity.student;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.itant.view.pulltorefresh.ILoadingLayout;
import com.itant.view.pulltorefresh.PullToRefreshBase;
import com.itant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.itant.view.pulltorefresh.PullToRefreshListView;
import com.onion.paper.R;
import com.onion.paper.adapter.QuestionnaireAdapter;
import com.onion.paper.adapter.QuestionnaireAdapter.OnQuestionnaireClickListener;
import com.onion.paper.model.web.MonitorStudentMap;
import com.onion.paper.model.web.Paper;
import com.onion.paper.model.web.PaperUserMap;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.CustomedActionBar.OnRightIconClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;

public class QuestionnaireListActivity extends Activity {

	private PullToRefreshListView ptrlv_questionnaire_list;
	private QuestionnaireAdapter mQuestionnaireAdapter;
	private Context mContext;
	private List<Paper> mQuestionnaires;
	
	private CustomedActionBar ab_questionnaire_list;
	
	private String mMonitorObjectId;
	private String mCurrentUserId;
	
	private ProgressDialogUtils pDlgUtl;

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
		setContentView(R.layout.activity_questionnaire_list);

		initComponent();
	}

	private void initComponent() {
		setTitle(R.string.label_questionnaire_list);
		mContext = getApplicationContext();

		ab_questionnaire_list = (CustomedActionBar) findViewById(R.id.ab_questionnaire_list);
		// 点击左侧图标
		ab_questionnaire_list.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onLeftIconClick() {
				onBackPressed();
			}
		});
		
		// 点击右侧图标
		ab_questionnaire_list.setOnRightIconClickListener(new OnRightIconClickListener() {
			
			@Override
			public void onRightIconClick() {
				// 取消关注
				if (HttpTool.isNetworkConnected(QuestionnaireListActivity.this)) {
					cancelFavoriteDialog();
				} else {
					Toast.makeText(QuestionnaireListActivity.this, R.string.label_check_network, Toast.LENGTH_SHORT).show();
				}
				
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
		mQuestionnaireAdapter = new QuestionnaireAdapter(mContext);
		mQuestionnaires = new ArrayList<>();
		mQuestionnaireAdapter.setQuestionnaires(mQuestionnaires);
		ptrlv_questionnaire_list.setAdapter(mQuestionnaireAdapter);
		ptrlv_questionnaire_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (HttpTool.isNetworkConnected(QuestionnaireListActivity.this)) {
					findQuestionnairesByObjectId(getIntent().getStringExtra("objectId"));
				} else {
					Toast.makeText(QuestionnaireListActivity.this, R.string.label_check_network, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});

		mQuestionnaireAdapter
				.setOnQuestionnaireClickListener(new OnQuestionnaireClickListener() {

					@Override
					public void onQuestionnaireClick(Paper paper) {
						// 点击进去填写某一问卷
						/*if (paper.getHasSubmit()) {
							// 已经提交过了
						}*/
						Intent answerIntent = new Intent(QuestionnaireListActivity.this, QuestionnaireDetailActivity.class);
						answerIntent.putExtra("paper", paper);
						startActivityForResult(answerIntent, 6);
					}
				});
		
		mCurrentUserId = BmobUser.getCurrentUser(this).getObjectId();
		mMonitorObjectId = getIntent().getStringExtra("objectId");
		if (HttpTool.isNetworkConnected(mContext)) {
			findQuestionnairesByObjectId(mMonitorObjectId);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (HttpTool.isNetworkConnected(this)) {
			if (resultCode == 6) {
				findQuestionnairesByObjectId(getIntent().getStringExtra("objectId"));
			}
		}
	}
	
	/**
	 * 根据创建者Id到Paper表查找该创建者发布的所有问卷
	 * @param objectId
	 */
	private void findQuestionnairesByObjectId(String objectId) {
		ptrlv_questionnaire_list.onRefreshComplete();
		// TODO Auto-generated method stub
		if (pDlgUtl == null) {
			pDlgUtl = new ProgressDialogUtils(QuestionnaireListActivity.this);
			pDlgUtl.show();
		}
		BmobQuery<Paper> maps = new BmobQuery<Paper>();
		maps.addWhereEqualTo("userId", objectId);
		maps.findObjects(this, new FindListener<Paper>() {
			
			@Override
			public void onSuccess(List<Paper> resultMaps) {
				
				// TODO Auto-generated method stub
				findPapersByMaps(resultMaps);
			}

			@Override
			public void onError(int arg0, String arg1) {
				if (pDlgUtl != null) {
					pDlgUtl.hide();
				}
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * 根据Paper的Id到PaperUserMap寻找该paper与当前用户的关系表（是否已经提交）
	 */
	private void findPapersByMaps(final List<Paper> papers) {
		// TODO Auto-generated method stub
		List<BmobQuery<PaperUserMap>> orQueries = new ArrayList<BmobQuery<PaperUserMap>>();
		for (Paper paper : papers) {
			BmobQuery<PaperUserMap> query = new BmobQuery<PaperUserMap>();
			query.addWhereEqualTo("paperId", paper.getObjectId());
			query.addWhereEqualTo("userId", BmobUser.getCurrentUser(QuestionnaireListActivity.this).getObjectId());
			orQueries.add(query);
		}
		
		BmobQuery<PaperUserMap> mainQuery = new BmobQuery<PaperUserMap>();
		mainQuery.or(orQueries);
		mainQuery.findObjects(QuestionnaireListActivity.this, new FindListener<PaperUserMap>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (pDlgUtl != null) {
					pDlgUtl.hide();
				}
			}

			@Override
			public void onSuccess(List<PaperUserMap> maps) {
				// TODO Auto-generated method stub
				if (pDlgUtl != null) {
					pDlgUtl.hide();
				}
				for (PaperUserMap map : maps) {
					for (Paper paper : papers) {
						if (TextUtils.equals(map.getPaperId(), paper.getObjectId())) {
							paper.setHasSubmit(map.getIfSubmit());
						}
					}
				}
				
				if (mQuestionnaires != null && mQuestionnaires.size() > 0) {
					mQuestionnaires.clear();
				}
				mQuestionnaires.addAll(papers);
				mQuestionnaireAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 取消关注对话框
	 * @param question
	 */
	private void cancelFavoriteDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(QuestionnaireListActivity.this).create();
		dialog.show();
		dialog.setContentView(R.layout.dialog_customed);
		
		TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_desc = (TextView) dialog.findViewById(R.id.tv_dialog_desc);
		tv_dialog_title.setText(R.string.label_delete_follow);
		tv_dialog_desc.setText(R.string.label_delete_follow_desc);
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 删除关联
				if (pDlgUtl == null) {
					pDlgUtl = new ProgressDialogUtils(QuestionnaireListActivity.this);
					pDlgUtl.show();
				}
				BmobQuery<MonitorStudentMap> query = new BmobQuery<MonitorStudentMap>();
				query.addWhereEqualTo("monitorId", mMonitorObjectId);
				query.addWhereEqualTo("studentId", mCurrentUserId);
				query.findObjects(QuestionnaireListActivity.this, new FindListener<MonitorStudentMap>() {

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
					}

					@Override
					public void onSuccess(List<MonitorStudentMap> maps) {
						// TODO Auto-generated method stub
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						if (maps != null && maps.size() == 1) {
							MonitorStudentMap map = maps.get(0);
							// 删除班长-学生关联
							map.delete(QuestionnaireListActivity.this);
							dialog.cancel();
							setResult(6);
							QuestionnaireListActivity.this.finish();
						}
					}
				});
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
}
